package br.com.agrologqueue.api.utils;

import br.com.agrologqueue.api.exceptions.UnauthorizedAccessException;
import br.com.agrologqueue.api.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * Obtém o objeto User (usuário logado) do contexto de segurança.
     * * @return A entidade User autenticada.
     * @throws UnauthorizedAccessException se nenhum usuário válido estiver autenticado.
     */
    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null ||
                !(authentication.getPrincipal() instanceof User)) {
            throw new UnauthorizedAccessException("Usuário logado não encontrado ou contexto inválido.");
        }

        return (User) authentication.getPrincipal();
    }
}