package com.example.chatapp.exception;

import com.example.chatapp.service.LogService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.example.chatapp.exception.BusinessError.ACCOUNT_DISABLED;
import static com.example.chatapp.exception.BusinessError.BAD_CREDENTIALS;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogService logService;

    // AuthService -> login()
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException ex) {
        String msg = ex.getMessage();
        logService.logError(msg);
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(msg)
                                .build()
                );
    }

    // AuthService -> login()
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException ex) {
        String msg = ex.getMessage();
        logService.logError(msg);
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .errorDescription(BAD_CREDENTIALS.getDescription())
                                .error(msg)
                                .build()
                );
    }

    //EmailService -> sendEmail()
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException ex) {
        String msg = ex.getMessage();
        logService.logError(msg);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(msg)
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String msg = ex.getMessage();
        logService.logError(msg);
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        String msg = ex.getMessage();
        logService.logError(msg);
        return ResponseEntity
                .status(CONFLICT)
                .body(
                        ExceptionResponse.builder()
                                .error(msg)
                                .build()
                );
    }

    @ExceptionHandler(RoleNotInitialized.class)
    public ResponseEntity<ExceptionResponse> handleRoleNotInitialized(RoleNotInitialized ex) {
        String msg = ex.getMessage();
        logService.logError(msg);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(msg)
                                .build()
                );
    }
}
