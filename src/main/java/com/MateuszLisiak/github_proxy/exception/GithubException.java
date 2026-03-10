package com.MateuszLisiak.github_proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GithubException extends RuntimeException {
    private final int status;
    private final HttpStatus httpStatus;

    public GithubException(String message, int status, HttpStatus httpStatus) {
        super(message);
        this.status = status;
        this.httpStatus = httpStatus;
    }

    public String getClientMessage() {
        return switch (httpStatus) {
            case NOT_FOUND -> "Repository not found on GitHub";
            case UNAUTHORIZED -> "Unauthorized access to GitHub API";
            default -> "GitHub API error";
        };
    }
}