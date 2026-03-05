package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.service.GithubService;
import com.MateuszLisiak.github_proxy.model.Dto.RepoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GithubController {
    private final GithubService githubService;

    @GetMapping("/repositories/{owner}/{repository-name}")
    public RepoDto getRepo(
            @PathVariable("owner") String owner,
            @PathVariable("repository-name") String repo
    ) {
        return githubService.getRepo(owner, repo);
    }
}