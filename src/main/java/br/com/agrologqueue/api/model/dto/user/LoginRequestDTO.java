package br.com.agrologqueue.api.model.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank
    String loginIdentifier,

    @NotBlank
    String password
) {}