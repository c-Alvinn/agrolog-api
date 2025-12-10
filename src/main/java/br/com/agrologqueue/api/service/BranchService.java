package br.com.agrologqueue.api.service;

import br.com.agrologqueue.api.model.dto.branch.BranchRequestDTO;
import br.com.agrologqueue.api.model.dto.branch.BranchResponseDTO;
import br.com.agrologqueue.api.exceptions.ResourceNotFoundException;
import br.com.agrologqueue.api.exceptions.ValidationException;
import br.com.agrologqueue.api.exceptions.UnauthorizedAccessException;
import br.com.agrologqueue.api.model.entity.Branch;
import br.com.agrologqueue.api.model.entity.Company;
import br.com.agrologqueue.api.model.entity.User;
import br.com.agrologqueue.api.repository.BranchRepository;
import br.com.agrologqueue.api.repository.CompanyRepository;
import br.com.agrologqueue.api.utils.SecurityUtils;
import br.com.agrologqueue.api.model.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final SecurityUtils securityUtils;

    public BranchService(
            BranchRepository branchRepository,
            CompanyRepository companyRepository,
            SecurityUtils securityUtils
    ) {
        this.branchRepository = branchRepository;
        this.companyRepository = companyRepository;
        this.securityUtils = securityUtils;
    }

    private void validateInternalScope(Long targetCompanyId) {
        User loggedUser = securityUtils.getLoggedUser();

        if (loggedUser.getRole() == Role.ADMIN) {
            return;
        }

        if (loggedUser.getRole() == Role.MANAGER ||
                loggedUser.getRole() == Role.SCALE_OPERATOR ||
                loggedUser.getRole() == Role.GATE_KEEPER) {

            if (loggedUser.getCompany() == null ||
                    !loggedUser.getCompany().getId().equals(targetCompanyId)) {

                String action = loggedUser.getRole() == Role.MANAGER ? "modificar/acessar" : "acessar";

                throw new ValidationException(
                        String.format("Usuário (%s) sem permissão para %s filiais fora do escopo da sua empresa.",
                                loggedUser.getRole().name(), action)
                );
            }
        }
    }

    @Transactional
    public BranchResponseDTO create(BranchRequestDTO dto) {
        validateInternalScope(dto.companyId());

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", dto.companyId()));

        if (branchRepository.existsByCode(dto.branchCode())) {
            throw new ValidationException("O código de filial '" + dto.branchCode() + "' já está em uso.");
        }

        Branch branch = new Branch();
        branch.setName(dto.name());
        branch.setAddress(dto.address());
        branch.setCode(dto.branchCode());
        branch.setCompany(company);

        return toResponseDTO(branchRepository.save(branch));
    }

    @Transactional
    public BranchResponseDTO update(Long id, BranchRequestDTO dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        validateInternalScope(branch.getCompany().getId());

        if (!branch.getCompany().getId().equals(dto.companyId())) {
            validateInternalScope(dto.companyId());

            Company newCompany = companyRepository.findById(dto.companyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", dto.companyId()));

            branch.setCompany(newCompany);
        }

        if (!branch.getCode().equals(dto.branchCode()) && branchRepository.existsByCode(dto.branchCode())) {
            throw new ValidationException("O código de filial '" + dto.branchCode() + "' já está em uso.");
        }

        branch.setName(dto.name());
        branch.setAddress(dto.address());
        branch.setCode(dto.branchCode());

        return toResponseDTO(branchRepository.save(branch));
    }

    @Transactional
    public void delete(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        validateInternalScope(branch.getCompany().getId());

        branchRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BranchResponseDTO findById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        validateInternalScope(branch.getCompany().getId());

        return toResponseDTO(branch);
    }

    @Transactional(readOnly = true)
    public List<BranchResponseDTO> findAll() {
        User loggedUser = securityUtils.getLoggedUser();
        Role role = loggedUser.getRole();

        if (role == Role.ADMIN) {
            return branchRepository.findAll().stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        if (role == Role.MANAGER || role == Role.SCALE_OPERATOR || role == Role.GATE_KEEPER) {
            if (loggedUser.getCompany() == null) {
                return List.of();
            }
            return branchRepository.findByCompanyId(loggedUser.getCompany().getId()).stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        throw new UnauthorizedAccessException("Este endpoint não é acessível para sua Role. Use o endpoint de busca filtrada (/branches/company/{companyId}).");
    }

    @Transactional(readOnly = true)
    public List<BranchResponseDTO> findBranchesByCompanyId(Long companyId) {
        if (companyId == null) {
            throw new ValidationException("O ID da Empresa é obrigatório para esta busca.");
        }

        return branchRepository.findByCompanyId(companyId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private BranchResponseDTO toResponseDTO(Branch branch) {
        return new BranchResponseDTO(
                branch.getId(),
                branch.getName(),
                branch.getAddress(),
                branch.getCode(),
                branch.getCompany().getId(),
                branch.getCompany().getName()
        );
    }
}