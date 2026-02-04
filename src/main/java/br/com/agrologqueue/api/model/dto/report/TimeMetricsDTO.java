package br.com.agrologqueue.api.model.dto.report;

public record TimeMetricsDTO(
        Long branchId,
        Double averageTimeMinutes,
        long totalCompleted,
        String periodLabel
) {}