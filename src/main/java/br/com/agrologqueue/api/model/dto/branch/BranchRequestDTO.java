package br.com.agrologqueue.api.model.dto.branch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BranchRequestDTO(
        @NotBlank(message = "O nome da filial é obrigatório.")
        String name,

        @NotBlank(message = "O endereço é obrigatório.")
        String address,

        @NotBlank(message = "O código de identificação da filial é obrigatório.")
        @Pattern(regexp = "^[A-Z]{2}-\\d{3}$",
                message = "O código da filial deve seguir o padrão: XX-999 (Ex: SP-001).")
        String branchCode,

        @NotNull(message = "O ID da empresa vinculada é obrigatório.")
        Long companyId
) {}