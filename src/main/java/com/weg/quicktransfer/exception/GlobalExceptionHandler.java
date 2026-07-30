package com.weg.quicktransfer.exception;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.weg.quicktransfer.dto.error.ErrorResponseDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildError(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials.",
                        request));
    }

    @ExceptionHandler(UserNotAllowdException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotAllowedException(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildError(
                        HttpStatus.FORBIDDEN,
                        "The user is not authorized",
                        request));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponseDTO> handleSQLException(
            SQLException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal database error.", request));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponseDTO> handleMessagingException(
            MessagingException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Email sending error.",
                        request));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    @ExceptionHandler(FirstLoginException.class)
    public ResponseEntity<ErrorResponseDTO> handleFirstLoginException(
            FirstLoginException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request));
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidUsername(
            InvalidUsernameException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPassword(
            InvalidPasswordException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidEmail(
            InvalidEmailException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .badRequest()
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected internal error occurred.",
                        request));
    }

    private ErrorResponseDTO buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        return new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());
    }
}
