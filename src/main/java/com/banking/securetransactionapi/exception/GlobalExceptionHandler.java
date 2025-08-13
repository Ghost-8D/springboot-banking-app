package com.banking.securetransactionapi.exception;

import com.banking.securetransactionapi.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Account not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "ACCOUNT_NOT_FOUND",
            ex.getMessage(),
            "The specified account does not exist in the system",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(
            InsufficientFundsException ex, HttpServletRequest request) {
        
        log.warn("Insufficient funds: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "INSUFFICIENT_FUNDS",
            ex.getMessage(),
            "The account does not have enough balance to complete this transaction",
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransfer(
            InvalidTransferException ex, HttpServletRequest request) {
        
        log.warn("Invalid transfer: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "INVALID_TRANSFER",
            ex.getMessage(),
            "The transfer operation is not valid (e.g., transferring to same account)",
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(
            UsernameAlreadyExistsException ex, HttpServletRequest request) {
        
        log.warn("Username already exists: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "USERNAME_ALREADY_EXISTS",
            ex.getMessage(),
            "Please choose a different username",
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", fieldErrors);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Invalid request data",
            fieldErrors,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String violations = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        log.warn("Constraint violation: {}", violations);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "CONSTRAINT_VIOLATION",
            "Request validation failed",
            violations,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        
        log.warn("Bad credentials: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "INVALID_CREDENTIALS",
            "Invalid username or password",
            "Please check your credentials and try again",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        log.warn("Authentication error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "AUTHENTICATION_FAILED",
            "Authentication failed",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("Unexpected runtime error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            "Please try again later or contact support if the problem persists",
            request.getRequestURI()
        );
        
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "UNKNOWN_ERROR",
            "An unexpected error occurred",
            "Please try again later or contact support if the problem persists",
            request.getRequestURI()
        );
        
        return ResponseEntity.internalServerError().body(error);
    }
}