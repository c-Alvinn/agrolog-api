package br.com.agrologqueue.api.service;

import br.com.agrologqueue.api.exceptions.ResourceNotFoundException;
import br.com.agrologqueue.api.exceptions.UnauthorizedAccessException;
import br.com.agrologqueue.api.model.dto.report.QueueStatusReportDTO;
import br.com.agrologqueue.api.model.entity.Branch;
import br.com.agrologqueue.api.model.entity.Schedule;
import br.com.agrologqueue.api.model.entity.User;
import br.com.agrologqueue.api.model.enums.QueueStatus;
import br.com.agrologqueue.api.model.enums.ReportPeriod;
import br.com.agrologqueue.api.model.enums.Role;
import br.com.agrologqueue.api.repository.BranchRepository;
import br.com.agrologqueue.api.repository.ScheduleRepository;
import br.com.agrologqueue.api.utils.PdfReportGenerator;
import br.com.agrologqueue.api.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReportingService {

    private final ScheduleRepository scheduleRepository;
    private final BranchRepository branchRepository;
    private final SecurityUtils securityUtils;

    public ReportingService(ScheduleRepository scheduleRepository, BranchRepository branchRepository, SecurityUtils securityUtils) {
        this.scheduleRepository = scheduleRepository;
        this.branchRepository = branchRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public QueueStatusReportDTO getQueueStatusByBranch(Long branchId) {
        User loggedUser = securityUtils.getLoggedUser();

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", branchId));

        if (loggedUser.getRole() != Role.ADMIN) {
            if (loggedUser.getCompany() == null ||
                    !branch.getCompany().getId().equals(loggedUser.getCompany().getId())) {
                throw new UnauthorizedAccessException("Você não tem permissão para visualizar relatórios desta filial.");
            }
        }

        long scheduled = scheduleRepository.countByBranchIdAndQueueStatus(branchId, QueueStatus.SCHEDULED);
        long inService = scheduleRepository.countByBranchIdAndQueueStatus(branchId, QueueStatus.IN_SERVICE);
        long completed = scheduleRepository.countByBranchIdAndQueueStatus(branchId, QueueStatus.COMPLETED);
        long canceled = scheduleRepository.countByBranchIdAndQueueStatus(branchId, QueueStatus.CANCELED);

        return new QueueStatusReportDTO(
                branch.getId(),
                branch.getName(),
                scheduled,
                inService,
                completed,
                canceled,
                (scheduled + inService)
        );
    }

    public void generatePerformanceReport(ReportPeriod period, HttpServletResponse response) throws Exception {
        User loggedUser = securityUtils.getLoggedUser();

        if (loggedUser.getCompany() == null) {
            throw new UnauthorizedAccessException("Usuário sem empresa vinculada.");
        }

        List<QueueStatus> statuses = List.of(QueueStatus.IN_SERVICE, QueueStatus.COMPLETED);

        List<Schedule> data = scheduleRepository.findReportData(
                loggedUser.getCompany().getId(),
                statuses,
                period.getStart(),
                period.getEnd()
        );

        PdfReportGenerator.generateSchedulesPdf(
                response,
                data,
                loggedUser.getCompany().getName(),
                period.getLabel()
        );
    }
}