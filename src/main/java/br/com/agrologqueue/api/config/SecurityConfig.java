package br.com.agrologqueue.api.config;

import br.com.agrologqueue.api.security.SecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/user/driver/register").permitAll();
                    req.requestMatchers(
                            "/v3/api-docs/**",       // Rota padrão (manter por segurança)
                            "/swagger-ui/**",        // Recursos visuais (CSS, JS)
                            "/swagger-ui.html",      // HTML legado
                            "/docs",                 // Sua interface personalizada
                            "/api-docs/**"           // <--- ADICIONE ESTA LINHA (Corrige o erro 403)
                    ).permitAll();

                    req.requestMatchers(HttpMethod.POST, "/user/internal/register").hasAnyRole("ADMIN", "MANAGER");
                    req.requestMatchers(HttpMethod.POST, "/user/carrier/register").hasAnyRole("ADMIN", "CARRIER");

                    req.requestMatchers(HttpMethod.POST, "/carrier").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/carrier/*").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/carrier/*").hasAnyRole("ADMIN", "CARRIER");
                    req.requestMatchers(HttpMethod.GET, "/carrier", "/carrier/*").authenticated();

                    req.requestMatchers(HttpMethod.POST, "/company").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/company/*").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/company/*").hasAnyRole("ADMIN", "MANAGER");
                    req.requestMatchers(HttpMethod.GET, "/company", "/companies/*").authenticated();

                    req.requestMatchers(HttpMethod.POST, "/branchy").hasAnyRole("ADMIN", "MANAGER");
                    req.requestMatchers(HttpMethod.PUT, "/branchy/*").hasAnyRole("ADMIN", "MANAGER");
                    req.requestMatchers(HttpMethod.DELETE, "/branchy/*").hasAnyRole("ADMIN", "MANAGER");
                    req.requestMatchers(HttpMethod.GET, "/branchy").hasAnyRole("ADMIN", "MANAGER", "SCALE_OPERATOR", "GATE_KEEPER");
                    req.requestMatchers(HttpMethod.GET, "/branchy/*").authenticated();
                    req.requestMatchers(HttpMethod.GET, "/branchy/company/*").authenticated();

                    req.requestMatchers(HttpMethod.POST, "/schedules").hasAnyRole("ADMIN", "MANAGER", "CARRIER", "SCALE_OPERATOR", "GATE_KEEPER");
                    req.requestMatchers(HttpMethod.GET, "/schedules", "/schedules/*").authenticated();
                    req.requestMatchers(HttpMethod.PATCH, "/schedules/{id}/in-service", "/schedules/{id}/completed").hasAnyRole("ADMIN", "MANAGER", "SCALE_OPERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/schedules/{id}/cancel").hasAnyRole("ADMIN", "MANAGER", "SCALE_OPERATOR", "GATE_KEEPER", "DRIVER");
                    req.requestMatchers(HttpMethod.DELETE, "/schedules/{id}").hasRole("ADMIN");

                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}