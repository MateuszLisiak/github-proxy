package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.exception.GithubException;
import com.MateuszLisiak.github_proxy.model.dto.ErrorDto;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GithubException.class)
    public ResponseEntity<ErrorDto> handleGithubException(GithubException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), LocalDateTime.now(), ex.getStatus(), Collections.emptyList());
        log.error("GithubException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(errorDto);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorDto> handleRetryableException(RetryableException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), LocalDateTime.now(), 503, Collections.emptyList());
        log.error("RetryableException (503): {}", ex.getMessage());
        return ResponseEntity.status(503).body(errorDto);
    }
}