package com.MateuszLisiak.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends ApplicationException {
    public RepositoryNotFoundException(String repoName) {
        super("Repository with name '" + repoName + "' not found", 404, HttpStatus.NOT_FOUND);
    }
}