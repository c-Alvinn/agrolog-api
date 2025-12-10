package br.com.agrologqueue.api.model.dto.carrier;

import jakarta.validation.constraints.NotBlank;

public record CarrierRequestDTO(
        @NotBlank(message = "O nome da transportadora é obrigatório.")
        String name
) {}
