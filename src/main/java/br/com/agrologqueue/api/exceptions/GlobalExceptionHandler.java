package br.com.agrologqueue.api.exceptions;

import br.com.agrologqueue.api.model.dto.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // HTTP 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // HTTP 400
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<StandardError> handleValidationException(ValidationException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // HTTP 403
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<StandardError> handleUnauthorizedAccess(UnauthorizedAccessException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // HTTP 400 (incluindo @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String validationMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining("; "));

        StandardError error = new StandardError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                validationMessages,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // HTTP 400 (ConstraintViolationException)
    @ExceptionHandler({ ConstraintViolationException.class, DataIntegrityViolationException.class })
    public ResponseEntity<StandardError> handleConstraintViolation(Exception e, HttpServletRequest request) {

        String detailMessage = "Violação de restrição de dados.";

        if (e.getCause() instanceof ConstraintViolationException constraintException) {
            String constraintName = constraintException.getConstraintName();

            if (constraintName != null && constraintName.toLowerCase().contains("cpf")) {
                detailMessage = "O CPF informado já existe no sistema.";
            } else if (constraintName != null && constraintName.toLowerCase().contains("username")) {
                detailMessage = "O Nome de Usuário (username) já está em uso.";
            } else if (constraintName != null && constraintName.toLowerCase().contains("unique")) {
                detailMessage = "Campo já existente. Verifique se os dados são únicos.";
            } else {
                detailMessage = constraintException.getMessage();
            }
        } else if (e instanceof DataIntegrityViolationException dataException) {
            detailMessage = "Falha de integridade: Certifique-se de que todos os campos obrigatórios foram preenchidos corretamente.";
        }

        StandardError error = new StandardError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                detailMessage,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}