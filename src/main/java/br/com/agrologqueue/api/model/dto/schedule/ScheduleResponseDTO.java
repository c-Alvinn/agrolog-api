package br.com.agrologqueue.api.model.dto.schedule;

import br.com.agrologqueue.api.model.enums.*;
import java.time.LocalDateTime;

public record ScheduleResponseDTO(
        Long id,

        Long branchId,
        String branchName,
        Long driverId,
        String driverName,
        Long carrierId,
        String carrierName,

        GrainType grainType,
        OperationType operationType,
        String licensePlate,
        TruckType truckType,

        QueueStatus queueStatus,
        Integer queuePosition,

        LocalDateTime scheduledAt,
        LocalDateTime calledAt,
        LocalDateTime releasedAt
) {}