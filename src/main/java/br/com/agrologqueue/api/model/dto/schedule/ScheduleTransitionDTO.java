package br.com.agrologqueue.api.model.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScheduleTransitionDTO(

        @NotBlank(message = "A placa do veículo é obrigatória.")
        @Size(min = 7, max = 8, message = "A placa deve ter entre 7 e 8 caracteres.")
        String licensePlate,

        @NotBlank(message = "O nome da filial é obrigatório.")
        String branchName
) {}