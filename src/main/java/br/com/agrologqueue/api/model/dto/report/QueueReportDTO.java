package br.com.agrologqueue.api.model.dto.report;

import br.com.agrologqueue.api.model.enums.QueueStatus;
import java.util.Map;

public record QueueReportDTO(
        Long branchId,
        String branchName,
        Map<QueueStatus, Long> statusCounts,
        long totalActive
) {}