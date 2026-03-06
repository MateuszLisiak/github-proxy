package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.service.GithubService;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GithubController {
    private final GithubService githubService;

    @GetMapping("/repositories/{owner}/{repository-name}")
    public RepoDto getRepo(@PathVariable("owner") String owner, @PathVariable("repository-name") String repo) {
        log.info("Received GET request for repository: '{}' by: '{}'", repo,owner);
        RepoDto result = githubService.getRepo(owner, repo);
        log.info("Returning RepoDto with repo name: '{}'", repo);
        return result;
    }
}