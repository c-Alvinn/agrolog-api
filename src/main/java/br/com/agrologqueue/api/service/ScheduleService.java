package br.com.agrologqueue.api.service;

import br.com.agrologqueue.api.exceptions.ResourceNotFoundException;
import br.com.agrologqueue.api.exceptions.UnauthorizedAccessException;
import br.com.agrologqueue.api.exceptions.ValidationException;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleRequestDTO;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleResponseDTO;
import br.com.agrologqueue.api.model.dto.schedule.ScheduleTransitionDTO;
import br.com.agrologqueue.api.model.entity.*;
import br.com.agrologqueue.api.model.enums.QueueStatus;
import br.com.agrologqueue.api.model.enums.Role;
import br.com.agrologqueue.api.repository.*;
import br.com.agrologqueue.api.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CarrierRepository carrierRepository;
    private final SecurityUtils securityUtils;
    private final ScheduleHistoryRepository historyRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, BranchRepository branchRepository, UserRepository userRepository, CarrierRepository carrierRepository, SecurityUtils securityUtils, ScheduleHistoryRepository historyRepository) {
        this.scheduleRepository = scheduleRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
        this.carrierRepository = carrierRepository;
        this.securityUtils = securityUtils;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public ScheduleResponseDTO create(ScheduleRequestDTO dto) {
        User loggedUser = securityUtils.getLoggedUser();
        Role userRole = loggedUser.getRole();

        User driver = getScheduleDriver(loggedUser, dto);

        Branch branch = switch (userRole) {
            case MANAGER, SCALE_OPERATOR, GATE_KEEPER -> validateInternalScopeAndGetBranch(loggedUser, dto);
            default -> getBranch(dto.branchId());
        };

        Carrier carrier = null;
        if (userRole == Role.CARRIER) {
            carrier = loggedUser.getCarrier();
        } else {
            Long carrierId = dto.carrierId() != null ? dto.carrierId() :
                    (driver.getCarrier() != null ? driver.getCarrier().getId() : null);
            carrier = (carrierId != null) ? getCarrier(carrierId) : null;
        }

        Schedule schedule = mapToSchedule(dto, branch, driver, carrier);

        Integer nextQueuePosition = scheduleRepository.findMaxQueuePosition(branch.getId())
                .map(maxPos -> maxPos + 1)
                .orElse(1);

        schedule.setQueuePosition(nextQueuePosition);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        registerHistory(savedSchedule, null, QueueStatus.SCHEDULED, loggedUser);
        return toResponseDTO(savedSchedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> findAll() {
        User loggedUser = securityUtils.getLoggedUser();
        List<Schedule> schedules;

        if (loggedUser.getRole() == Role.ADMIN) {
            schedules = scheduleRepository.findAll();

        } else if (loggedUser.getRole() == Role.MANAGER ||
                loggedUser.getRole() == Role.SCALE_OPERATOR ||
                loggedUser.getRole() == Role.GATE_KEEPER) {

            if (loggedUser.getCompany() == null) return List.of();
            schedules = scheduleRepository.findByBranch_Company_Id(loggedUser.getCompany().getId());

        } else if (loggedUser.getRole() == Role.CARRIER) {
            if (loggedUser.getCarrier() == null) return List.of();
            schedules = scheduleRepository.findByCarrierId(loggedUser.getCarrier().getId());

        } else if (loggedUser.getRole() == Role.DRIVER) {
            schedules = scheduleRepository.findByDriverId(loggedUser.getId());

        } else {
            throw new UnauthorizedAccessException("Usuário não autorizado a visualizar a lista de agendamentos.");
        }

        return schedules.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDTO findById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));

        User loggedUser = securityUtils.getLoggedUser();
        Role userRole = loggedUser.getRole();
        Long loggedUserId = loggedUser.getId();

        if (userRole != Role.ADMIN) {

            boolean allowed = false;

            if ((userRole == Role.MANAGER || userRole == Role.SCALE_OPERATOR || userRole == Role.GATE_KEEPER) && loggedUser.getCompany() != null) {
                if (schedule.getBranch().getCompany().getId().equals(loggedUser.getCompany().getId())) {
                    allowed = true;
                }
            } else if (userRole == Role.CARRIER && loggedUser.getCarrier() != null) {
                if (schedule.getCarrier() != null && schedule.getCarrier().getId().equals(loggedUser.getCarrier().getId())) {
                    allowed = true;
                }
            } else if (userRole == Role.DRIVER) {
                if (schedule.getDriver().getId().equals(loggedUserId)) {
                    allowed = true;
                }
            }

            if (!allowed) {
                throw new UnauthorizedAccessException("Você não tem permissão para visualizar este agendamento.");
            }
        }

        return toResponseDTO(schedule);
    }

    @Transactional
    public void moveToInService(ScheduleTransitionDTO transition) {
        User loggedUser = securityUtils.getLoggedUser();
        Role userRole = loggedUser.getRole();

        if (userRole != Role.ADMIN && userRole != Role.MANAGER && userRole != Role.SCALE_OPERATOR) {
            throw new UnauthorizedAccessException("Somente usuários ADMIN, MANAGER ou SCALE_OPERATOR podem mover agendamentos para IN_SERVICE.");
        }

        Schedule schedule = findActiveScheduleByPlateAndBranch(transition, loggedUser);

        if (schedule.getQueueStatus() != QueueStatus.SCHEDULED) {
            throw new ValidationException(String.format(
                    "O agendamento da placa %s não está no status SCHEDULED, mas sim em %s. Não pode ser movido para IN_SERVICE.",
                    transition.licensePlate(), schedule.getQueueStatus().name())
            );
        }

        QueueStatus oldStatus = schedule.getQueueStatus();
        Integer oldQueuePosition = schedule.getQueuePosition();

        schedule.setQueueStatus(QueueStatus.IN_SERVICE);
        schedule.setCalledAt(LocalDateTime.now());
        schedule.setQueuePosition(null);

        scheduleRepository.save(schedule);

        if (oldQueuePosition != null) {
            scheduleRepository.reorderQueuePositions(schedule.getBranch().getId(), oldQueuePosition);
        }

        registerHistory(schedule, oldStatus, QueueStatus.IN_SERVICE, loggedUser);
    }

    @Transactional
    public void moveToCompleted(ScheduleTransitionDTO transition) {
        User loggedUser = securityUtils.getLoggedUser();
        Role userRole = loggedUser.getRole();

        if (userRole != Role.ADMIN && userRole != Role.MANAGER && userRole != Role.SCALE_OPERATOR) {
            throw new UnauthorizedAccessException("Somente usuários ADMIN, MANAGER ou SCALE_OPERATOR podem marcar agendamentos como COMPLETED.");
        }

        Schedule schedule = findActiveScheduleByPlateAndBranch(transition, loggedUser);

        if (schedule.getQueueStatus() != QueueStatus.IN_SERVICE) {
            throw new ValidationException(String.format(
                    "O agendamento da placa %s não está no status IN_SERVICE, mas sim em %s. Não pode ser movido para COMPLETED.",
                    transition.licensePlate(), schedule.getQueueStatus().name())
            );
        }

        QueueStatus oldStatus = schedule.getQueueStatus();

        schedule.setQueueStatus(QueueStatus.COMPLETED);
        schedule.setReleasedAt(LocalDateTime.now());

        scheduleRepository.save(schedule);

        registerHistory(schedule, oldStatus, QueueStatus.IN_SERVICE, loggedUser);
    }

    @Transactional
    public void cancel(ScheduleTransitionDTO transition) {
        User loggedUser = securityUtils.getLoggedUser();
        Role userRole = loggedUser.getRole();

        if (userRole == Role.CARRIER) {
            throw new UnauthorizedAccessException("Usuários CARRIER não têm permissão para cancelar agendamentos.");
        }

        Schedule schedule = findActiveScheduleByPlateAndBranch(transition, loggedUser);

        if (userRole == Role.DRIVER && !schedule.getDriver().getId().equals(loggedUser.getId())) {
            throw new UnauthorizedAccessException("Você não tem permissão para cancelar agendamentos de outros motoristas.");
        }

        if (schedule.getQueueStatus() == QueueStatus.COMPLETED || schedule.getQueueStatus() == QueueStatus.CANCELED) {
            throw new ValidationException(String.format(
                    "O agendamento da placa %s está em %s e não pode ser cancelado.",
                    transition.licensePlate(), schedule.getQueueStatus().name())
            );
        }

        Integer oldQueuePosition = schedule.getQueuePosition();
        QueueStatus oldStatus = schedule.getQueueStatus();

        schedule.setQueueStatus(QueueStatus.CANCELED);
        schedule.setQueuePosition(null);

        scheduleRepository.save(schedule);

        if (oldQueuePosition != null) {
            scheduleRepository.reorderQueuePositions(schedule.getBranch().getId(), oldQueuePosition);
        }

        registerHistory(schedule, oldStatus, QueueStatus.CANCELED, loggedUser);
    }

    @Transactional
    public void delete(Long id) {
        User loggedUser = securityUtils.getLoggedUser();

        if (loggedUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Somente usuários ADMIN podem excluir agendamentos permanentemente.");
        }

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));

        if (schedule.getQueueStatus() == QueueStatus.SCHEDULED && schedule.getQueuePosition() != null) {
            scheduleRepository.reorderQueuePositions(schedule.getBranch().getId(), schedule.getQueuePosition());
        }

        scheduleRepository.delete(schedule);
    }

    private User getScheduleDriver(User loggedUser, ScheduleRequestDTO dto) {

        if (loggedUser.getRole() == Role.DRIVER) {
            return loggedUser;

        } else {
            if (dto.driverCpf() == null) {
                throw new ValidationException("O CPF do motorista é obrigatório para agendamentos não feitos por um motorista logado.");
            }

            User driver = userRepository.findByCpf(dto.driverCpf())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorista ", "cpf", dto.driverCpf()));

            if (driver.getRole() != Role.DRIVER) {
                throw new ValidationException("O CPF " + dto.driverCpf() + " não está vinculado a um motorista.");
            }
            return driver;
        }
    }

    private Branch validateExternalScopeAndGetBranch(ScheduleRequestDTO dto) {
        if (dto.branchId() == null) throw new ValidationException("O ID da Filial é obrigatório.");
        if (dto.companyId() == null) throw new ValidationException("O ID da Empresa é obrigatório.");

        Branch branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.branchId()));

        if (!branch.getCompany().getId().equals(dto.companyId())) {
            throw new ValidationException("A Filial " + branch.getName() + " não pertence à Empresa de ID " + dto.companyId() + ".");
        }
        return branch;
    }

    private Schedule findActiveScheduleByPlateAndBranch(ScheduleTransitionDTO transition, User loggedUser) {
        Schedule schedule = scheduleRepository.findLatestActiveByPlateAndBranch(
                transition.licensePlate().toUpperCase().trim(),
                transition.branchName()
        ).orElseThrow(() -> new ResourceNotFoundException(
                String.format("Nenhum agendamento ativo encontrado para a placa %s na filial %s.",
                        transition.licensePlate(), transition.branchName())
        ));

        if (loggedUser.getRole() != Role.ADMIN) {
            if (loggedUser.getCompany() != null) {
                if (!schedule.getBranch().getCompany().getId().equals(loggedUser.getCompany().getId())) {
                    throw new UnauthorizedAccessException("Você não tem permissão para operar agendamentos de outra empresa.");
                }
            }
        }

        return schedule;
    }

    private Branch validateInternalScopeAndGetBranch(User loggedUser, ScheduleRequestDTO dto) {
        if (loggedUser.getCompany() == null) {
            throw new ValidationException("Usuário interno não vinculado a uma Empresa. Agendamento não permitido.");
        }
        if (dto.branchId() == null) throw new ValidationException("O ID da Filial é obrigatório.");

        Branch branch = branchRepository.findById(dto.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", dto.branchId()));

        if (!branch.getCompany().getId().equals(loggedUser.getCompany().getId())) {
            throw new ValidationException(
                    String.format("%s não tem permissão para agendar em filiais fora da sua empresa (%s).",
                            loggedUser.getRole().name(), loggedUser.getCompany().getName())
            );
        }
        return branch;
    }

    private Carrier validateCarrierScopeAndGetCarrier(User loggedUser, ScheduleRequestDTO dto) {
        if (loggedUser.getCarrier() == null) {
            throw new ValidationException("Usuário não vinculado a uma Transportadora. Agendamento não permitido.");
        }
        return loggedUser.getCarrier();
    }

    private Schedule findAndValidateScope(Long scheduleId, User loggedUser) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        if (loggedUser.getRole() != Role.ADMIN) {
            if (loggedUser.getCompany() == null) {
                throw new UnauthorizedAccessException("Usuário interno sem vínculo de Empresa. Operação não autorizada.");
            }
            if (!schedule.getBranch().getCompany().getId().equals(loggedUser.getCompany().getId())) {
                throw new UnauthorizedAccessException("Você não tem permissão para operar agendamentos fora do escopo da sua empresa.");
            }
        }
        return schedule;
    }

    private Branch getBranch(Long branchId) {
        if (branchId == null) throw new ValidationException("O ID da Filial é obrigatório.");
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", branchId));
    }

    private Carrier getCarrier(Long carrierId) {
        if (carrierId == null) throw new ValidationException("O ID da Transportadora é obrigatório.");
        return carrierRepository.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier", "id", carrierId));
    }

    private Schedule mapToSchedule(ScheduleRequestDTO dto, Branch branch, User driver, Carrier carrier) {
        Schedule schedule = new Schedule();
        schedule.setBranch(branch);
        schedule.setDriver(driver);
        schedule.setCarrier(carrier);

        schedule.setGrainType(dto.grainType());
        schedule.setOperationType(dto.operationType());
        schedule.setLicensePlate(dto.licensePlate().toUpperCase().trim());
        schedule.setTruckType(dto.truckType());

        schedule.setQueueStatus(QueueStatus.SCHEDULED);
        schedule.setQueuePosition(null);

        return schedule;
    }

    private ScheduleResponseDTO toResponseDTO(Schedule schedule) {
        return new ScheduleResponseDTO(
                schedule.getId(),
                schedule.getBranch().getId(),
                schedule.getBranch().getName(),
                schedule.getDriver().getId(),
                schedule.getDriver().getName(),
                schedule.getCarrier() != null ? schedule.getCarrier().getId() : null,
                schedule.getCarrier() != null ? schedule.getCarrier().getName() : "Autônomo / Não Informado",
                schedule.getGrainType(),
                schedule.getOperationType(),
                schedule.getLicensePlate(),
                schedule.getTruckType(),
                schedule.getQueueStatus(),
                schedule.getQueuePosition(),
                schedule.getCreatedAt(),
                schedule.getCalledAt(),
                schedule.getReleasedAt()
        );
    }

    private void registerHistory(Schedule schedule, QueueStatus oldStatus, QueueStatus newStatus, User user) {
        if (oldStatus != newStatus) {
            ScheduleHistory history = new ScheduleHistory(schedule, oldStatus, newStatus, user);
            historyRepository.save(history);
        }
    }
}