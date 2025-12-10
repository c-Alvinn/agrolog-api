package br.com.agrologqueue.api.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterCarrierUserRequestDTO(

        @NotBlank(message = "O nome completo é obrigatório.")
        String name,

        @NotBlank(message = "O nome de usuário é obrigatório.")
        String username,

        @NotBlank(message = "A senha é obrigatória.")
        String password,

        @NotNull(message = "O ID da Transportadora é obrigatório.")
        Long carrierId
) {}
