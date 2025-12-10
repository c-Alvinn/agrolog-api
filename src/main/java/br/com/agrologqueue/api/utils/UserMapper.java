package br.com.agrologqueue.api.utils;

import br.com.agrologqueue.api.model.dto.user.UserRegistrationResponseDTO;
import br.com.agrologqueue.api.model.entity.User;
import br.com.agrologqueue.api.model.enums.Role;

public final class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não deve ser instanciada.");
    }

    /**
     * Converte a entidade User para o DTO de resposta simplificado para o registro.
     * * @param user A entidade User recém-criada.
     * @return UserRegistrationResponseDTO com dados essenciais.
     */
    public static UserRegistrationResponseDTO toRegistrationResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        String loginIdentifier = user.getUsername();
        if (loginIdentifier == null || loginIdentifier.isEmpty()) {
            if (user.getRole() == Role.DRIVER && user.getCpf() != null) {
                loginIdentifier = user.getCpf();
            } else {
                loginIdentifier = "N/A";
            }
        }

        return new UserRegistrationResponseDTO(
                user.getId(),
                user.getName(),
                loginIdentifier,
                user.getRole()
        );
    }
}