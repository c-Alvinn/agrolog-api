package br.com.agrologqueue.api.model.dto.user;

public record LoginResponseDTO(
        String token,
        UserResponseDTO user
) {}