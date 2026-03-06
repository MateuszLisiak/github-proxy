package com.MateuszLisiak.github_proxy.exception;

import lombok.Getter;
@Getter
public class GithubException extends RuntimeException {
    private final int status;
    public GithubException(String message, int status){
        super(message);
        this.status = status;
    }
}