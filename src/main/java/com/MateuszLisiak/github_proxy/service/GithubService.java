package com.MateuszLisiak.github_proxy.service;

import com.MateuszLisiak.github_proxy.client.GithubClient;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.mapper.RepoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubClient githubClient;
    private final RepoMapper repoMapper;

    public RepoDto getRepo(String owner, String repo) {
        return repoMapper.toDto(githubClient.getUserRepo(owner, repo));
    }
}