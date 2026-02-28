package com.scholara.identity.api;

import com.scholara.identity.domain.exception.AccountDisabledException;
import com.scholara.identity.domain.exception.EmailAlreadyExistsException;
import com.scholara.identity.domain.exception.InvalidCredentialsException;
import com.scholara.identity.domain.exception.InvalidOtpException;
import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.exception.TokenExpiredException;
import com.scholara.identity.domain.exception.UserNotFoundException;
import com.scholara.shared.error.ApiError;
import com.scholara.shared.error.ErrorCode;
import com.scholara.shared.error.ScholaraException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the identity module API.
 */
@RestControllerAdvice(basePackages = "com.scholara.identity.api")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(ErrorCode.INVALID_CREDENTIALS, request.getRequestURI()));
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ApiError> handleAccountDisabled(
            AccountDisabledException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(ErrorCode.ACCOUNT_DISABLED, request.getRequestURI()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(ErrorCode.EMAIL_ALREADY_EXISTS, request.getRequestURI()));
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiError> handleInvalidOtp(
            InvalidOtpException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(ErrorCode.INVALID_OTP, request.getRequestURI()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(ErrorCode.TOKEN_INVALID, request.getRequestURI()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(
            TokenExpiredException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(ErrorCode.TOKEN_EXPIRED, request.getRequestURI()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(ErrorCode.USER_NOT_FOUND, request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(ErrorCode.ACCESS_DENIED, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Invalid value",
                        (existing, replacement) -> existing // Keep first error for duplicate fields
                ));

        ApiError apiError = ApiError.withDetails(
                ErrorCode.VALIDATION_ERROR,
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(ErrorCode.INVALID_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ScholaraException.class)
    public ResponseEntity<ApiError> handleScholaraException(
            ScholaraException ex,
            HttpServletRequest request) {

        HttpStatus status = switch (ex.getErrorCode()) {
            case INVALID_CREDENTIALS, TOKEN_EXPIRED, TOKEN_INVALID, SESSION_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED, ACCOUNT_DISABLED, ACCOUNT_LOCKED, INSUFFICIENT_PERMISSIONS -> HttpStatus.FORBIDDEN;
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status)
                .body(ApiError.of(ex.getErrorCode(), ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(ErrorCode.INTERNAL_ERROR, request.getRequestURI()));
    }
}
