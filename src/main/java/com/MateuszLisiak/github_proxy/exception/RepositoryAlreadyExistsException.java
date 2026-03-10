package com.MateuszLisiak.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class RepositoryAlreadyExistsException extends ApplicationException {
    public RepositoryAlreadyExistsException(String owner, String repoName) {
        super("Repository '" + owner + "/" + repoName + "' already exists", 409, HttpStatus.CONFLICT);
    }
}