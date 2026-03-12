package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.exception.GithubProxyException;
import com.MateuszLisiak.github_proxy.exception.GithubException;
import com.MateuszLisiak.github_proxy.model.dto.ErrorDto;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GithubException.class)
    public ResponseEntity<ErrorDto> handleGithubException(GithubException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getHttpStatus(), ex.getClientMessage(), LocalDateTime.now());
        log.error("GithubException: '{}'", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(GithubProxyException.class)
    public ResponseEntity<ErrorDto> handleApplicationException(GithubProxyException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getHttpStatus(), ex.getMessage(), LocalDateTime.now());
        log.error("ApplicationException: '{}'", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(errorDto);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorDto> handleRetryableException(RetryableException ex) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.valueOf(ex.status()), ex.getMessage(), LocalDateTime.now());
        log.error("RetryableException (503): '{}'", ex.getMessage());
        return ResponseEntity.status(ex.status()).body(errorDto);
    }
}