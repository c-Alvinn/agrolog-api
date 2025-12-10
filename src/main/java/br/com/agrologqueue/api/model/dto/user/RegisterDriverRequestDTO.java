package br.com.agrologqueue.api.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterDriverRequestDTO(

    @NotBlank(message = "O nome é obrigatório.")
    String name,

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos.")
    String cpf,

    @NotBlank(message = "A senha é obrigatória.")
    String password,

    @NotBlank(message = "O número de telefone é obrigatório para contato.")
    String phoneNumber
) {}