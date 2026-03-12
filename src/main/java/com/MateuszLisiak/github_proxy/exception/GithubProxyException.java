package com.MateuszLisiak.github_proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubProxyException extends RuntimeException {
    private final int status;
    private final HttpStatus httpStatus;

    public GithubProxyException(String message, int status, HttpStatus httpStatus) {
        super(message);
        this.status = status;
        this.httpStatus = httpStatus;
    }
}