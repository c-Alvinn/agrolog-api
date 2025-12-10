package br.com.agrologqueue.api.service;

import br.com.agrologqueue.api.model.entity.User;
import br.com.agrologqueue.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByCpfAndActiveTrue(loginIdentifier);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        userOptional = userRepository.findByUsernameAndActiveTrue(loginIdentifier);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        throw new UsernameNotFoundException("Usuário não encontrado ou inativo: " + loginIdentifier);
    }
}
