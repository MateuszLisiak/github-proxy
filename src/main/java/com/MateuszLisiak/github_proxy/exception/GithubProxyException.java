package com.MateuszLisiak.github_proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubProxyException extends RuntimeException {
    private final HttpStatus httpStatus;

    public GithubProxyException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}