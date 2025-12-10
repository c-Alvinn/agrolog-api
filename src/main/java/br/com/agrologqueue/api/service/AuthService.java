package br.com.agrologqueue.api.service;

import br.com.agrologqueue.api.model.dto.user.LoginRequestDTO;
import br.com.agrologqueue.api.model.dto.user.LoginResponseDTO;
import br.com.agrologqueue.api.model.dto.user.UserResponseDTO;
import br.com.agrologqueue.api.model.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Processa a autenticação do usuário.
     * @param data O DTO de requisição contendo o loginIdentifier (CPF ou Username) e a senha.
     * @return O JWT gerado.
     */
    public LoginResponseDTO authenticate(LoginRequestDTO data) {

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                data.loginIdentifier(),
                data.password()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();

        return new LoginResponseDTO(tokenService.generateToken(user), mapUserToResponseDTO(user));
    }

    private UserResponseDTO mapUserToResponseDTO(User user) {

        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;
        String companyName = user.getCompany() != null ? user.getCompany().getName() : null;

        Long branchId = user.getBranch() != null ? user.getBranch().getId() : null;
        String branchName = user.getBranch() != null ? user.getBranch().getName() : null;

        Long carrierId = user.getCarrier() != null ? user.getCarrier().getId() : null;
        String carrierName = user.getCarrier() != null ? user.getCarrier().getName() : null;

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getCpf(),
                user.getPhoneNumber(),
                user.getRole(),
                companyId,
                companyName,
                branchId,
                branchName,
                carrierId,
                carrierName
        );
    }
}