package br.com.agrologqueue.api.model.dto.user;

import br.com.agrologqueue.api.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterInternalUserRequestDTO(

        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O nome de usuário é obrigatório.")
        String username,

        @Pattern(regexp = "^\\d{11}$|^$", message = "CPF deve ter 11 dígitos ou ser vazio.")
        String cpf,

        @NotBlank(message = "A senha é obrigatória.")
        String password,

        @NotNull(message = "O papel (Role) é obrigatório.")
        Role role,

        @NotNull(message = "O ID da filial é obrigatório.")
        Long branchId,

        @NotNull(message = "O ID da empresa é obrigatório.")
        Long companyId
) {}