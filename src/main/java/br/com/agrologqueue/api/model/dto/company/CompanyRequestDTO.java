package br.com.agrologqueue.api.model.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CompanyRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        String name,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @Pattern(regexp = "^\\d{14}$", message = "CNPJ deve ter 14 dígitos.")
        String cnpj
) {}