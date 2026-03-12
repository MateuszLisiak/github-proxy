package com.MateuszLisiak.github_proxy.exception;

import org.springframework.http.HttpStatus;

public class RepositoryAlreadyExistsException extends GithubProxyException {
    public RepositoryAlreadyExistsException(String owner, String repoName) {
        super("Repository '" + owner + "/" + repoName + "' already exists", HttpStatus.CONFLICT);
    }
}