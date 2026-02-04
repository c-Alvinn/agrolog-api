package br.com.agrologqueue.api.model.dto.schedule;

import br.com.agrologqueue.api.model.enums.GrainType;
import br.com.agrologqueue.api.model.enums.OperationType;
import br.com.agrologqueue.api.model.enums.TruckType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScheduleRequestDTO(

        @NotNull(message = "A Filial é obrigatório.")
        Long branchId,

        Long companyId,

        String driverCpf,

        @NotNull(message = "O tipo de grão é obrigatório.")
        GrainType grainType,

        @NotNull(message = "O tipo de operação é obrigatório.")
        OperationType operationType,

        Long carrierId,

        @NotBlank(message = "A placa do veículo é obrigatória.")
        String licensePlate,

        @NotNull(message = "O tipo de caminhão é obrigatório.")
        TruckType truckType
) {}