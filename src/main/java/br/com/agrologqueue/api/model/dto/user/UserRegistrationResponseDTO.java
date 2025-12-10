package br.com.agrologqueue.api.model.dto.user;

import br.com.agrologqueue.api.model.enums.Role;

public record UserRegistrationResponseDTO(
        Long id,
        String name,
        String username,
        Role role
) {
}
