package org.example.messmate.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.example.messmate.dto.ApiExceptionDto;
import org.example.messmate.dto.ValidationExceptionDto;
import org.example.messmate.exception.otp.InvalidOtpException;
import org.example.messmate.exception.otp.OtpResendTooSoonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AlreadyRatedException.class)
    public ResponseEntity<ApiExceptionDto> handleAlreadyRatedException(
            AlreadyRatedException ex,
            HttpServletRequest request
    ) {
        ApiExceptionDto apiExceptionDto =
                new ApiExceptionDto(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                apiExceptionDto,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiExceptionDto> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        ApiExceptionDto apiExceptionDto =
                new ApiExceptionDto(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                apiExceptionDto,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiExceptionDto> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                apiExceptionDto,
                HttpStatus.NOT_FOUND
        );

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiExceptionDto> handleNoHandlerFoundException(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<ApiExceptionDto> handleUserUnauthorizedException(
            UserUnauthorizedException ex,
            HttpServletRequest request
    ){
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return
                new ResponseEntity<>(
                    apiExceptionDto,
                    HttpStatus.UNAUTHORIZED
                );
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiExceptionDto> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    ){
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return
                new ResponseEntity<>(
                        apiExceptionDto,
                        HttpStatus.BAD_REQUEST
                );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiExceptionDto> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request
    ){
        ApiExceptionDto apiExceptionDto =
                new ApiExceptionDto(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(apiExceptionDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiExceptionDto> handleInvalidOtpException(
            InvalidOtpException ex,
            HttpServletRequest request
    ){
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return
                new ResponseEntity<>(
                        apiExceptionDto,
                        HttpStatus.BAD_REQUEST
                );
    }

    @ExceptionHandler(OtpResendTooSoonException.class)
    public ResponseEntity<ApiExceptionDto> handleOtpResendTooSoonException(
            OtpResendTooSoonException ex,
            HttpServletRequest request
    ){
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return
                new ResponseEntity<>(
                        apiExceptionDto,
                        HttpStatus.BAD_REQUEST
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiExceptionDto> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request
    ){
        ApiExceptionDto apiExceptionDto =
                new ApiExceptionDto(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(apiExceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiExceptionDto> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiExceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        ValidationExceptionDto validationExceptionDto = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "validation error",
                request.getRequestURI(),
                fieldErrors
        );
        return new ResponseEntity<>(validationExceptionDto, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong. Please try again later");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong. Please try again later");
    }
}
