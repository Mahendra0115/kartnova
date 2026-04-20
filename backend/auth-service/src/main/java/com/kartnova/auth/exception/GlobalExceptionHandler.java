package com.kartnova.auth.exception;

import com.kartnova.auth.dto.response.ErrorResponse;
import com.kartnova.auth.util.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ErrorResponse handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            BadCredentialsException.class
    })
    public ErrorResponse handleInvalidCredentials(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                AppConstants.ERROR_INVALID_CREDENTIALS,
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            AuthorizationDeniedException.class
    })
    public ErrorResponse handleUnauthorized(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                AppConstants.ERROR_VALIDATION_FAILED,
                request.getRequestURI(),
                validationErrors
        );
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                AppConstants.ERROR_INTERNAL_SERVER,
                request.getRequestURI(),
                null
        );
    }

    private ErrorResponse buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors
    ) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}