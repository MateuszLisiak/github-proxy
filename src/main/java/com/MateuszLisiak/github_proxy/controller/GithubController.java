package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.service.GithubService;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GithubController {
    private final GithubService githubService;

    @GetMapping("/repositories/{owner}/{repository-repositoryName}")
    public RepoDto getGithubRepo(@PathVariable("owner") String owner, @PathVariable("repository-repositoryName") String repoName) {
        log.info("Received GET request for repository: '{}' by: '{}'", repoName, owner);
        RepoDto result = githubService.getGithubRepo(owner, repoName);
        log.info("Returning RepoDto with repoName repositoryName: '{}'", repoName);
        return result;
    }

    @GetMapping("/local/repositories/{owner}/{repository-repositoryName}")
    public RepoDto getLocalRepo(@PathVariable("owner") String owner, @PathVariable("repository-repositoryName") String repoName) {
        log.info("Received GET request for local repository: '{}' by: '{}'", repoName, owner);
        RepoDto result = githubService.getLocalRepo(owner, repoName);
        log.info("Returning local RepoDto with repoName repositoryName: '{}'", repoName);
        return result;
    }

    @PutMapping("/repositories/{owner}/{repository-repositoryName}")
    public RepoDto putLocalRepo(@PathVariable("owner") String owner, @PathVariable("repository-repositoryName") String repoName) {
        log.info("Received PUT request for local repository: '{}' by: '{}'", repoName, owner);
        RepoDto result = githubService.putRepo(owner, repoName);
        log.info("Returning updated RepoDto with repoName repositoryName: '{}'", repoName);
        return result;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/repositories/{owner}/{repository-repositoryName}")
    public void deleteLocalRepo(@PathVariable("owner") String owner, @PathVariable("repository-repositoryName") String repoName) {
        log.info("Received DELETE request for local repository: '{}' by: '{}'", repoName, owner);
        githubService.deleteRepo(owner, repoName);
        log.info("Returning RepoDto for repository '{}' owned by '{}'", repoName, owner);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/repositories/{owner}/{repository-repositoryName}")
    public RepoDto postRepo(@PathVariable("owner") String owner, @PathVariable("repository-repositoryName") String repoName) {
        RepoDto result = githubService.postGithubRepo(owner, repoName);
        log.info("Repo added to local db: '{}'", repoName);
        return result;
    }
}