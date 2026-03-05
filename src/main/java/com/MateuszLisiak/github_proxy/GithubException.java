package com.MateuszLisiak.github_proxy;

import lombok.Getter;
@Getter
public class GithubException extends RuntimeException {
    private final int status;
    public GithubException(String message, int status){
        super(message);
        this.status = status;
    }
}