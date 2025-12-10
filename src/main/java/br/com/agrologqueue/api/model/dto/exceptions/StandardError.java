package br.com.agrologqueue.api.model.dto.exceptions;

import java.time.LocalDateTime;

public record StandardError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String path
) {}