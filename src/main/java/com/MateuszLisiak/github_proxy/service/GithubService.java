package com.MateuszLisiak.github_proxy.service;

import com.MateuszLisiak.github_proxy.client.GithubClient;
import com.MateuszLisiak.github_proxy.exception.RepositoryAlreadyExistsException;
import com.MateuszLisiak.github_proxy.exception.RepositoryNotFoundException;
import com.MateuszLisiak.github_proxy.model.GithubRepo;
import com.MateuszLisiak.github_proxy.model.Repo;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.mapper.RepoMapper;
import com.MateuszLisiak.github_proxy.repository.RepoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubClient githubClient;
    private final RepoMapper repoMapper;
    private final RepoRepository repository;

    public RepoDto getGithubRepo(String owner, String repoName) {
        return repoMapper.toDto(githubClient.getUserRepo(owner, repoName));
    }

    public RepoDto getLocalRepo(String owner, String repoName) {
        return repoMapper.toDto(repository.getByOwnerAndName(owner, repoName).orElseThrow(
                () -> new RepositoryNotFoundException(repoName)));
    }

    @Transactional
    public RepoDto postGithubRepo(String owner, String repoName) {
        if (repository.existsByOwnerAndName(owner, repoName)) {
            throw new RepositoryAlreadyExistsException(owner, repoName);
        }
        GithubRepo githubRepo = githubClient.getUserRepo(owner, repoName);
        Repo repo = repoMapper.toEntity(githubRepo);
        repo.setOwnerAndRepositoryName(githubRepo);
        Repo savedRepo = repository.save(repo);
        return repoMapper.toDto(savedRepo);
    }

    @Transactional
    public RepoDto putRepo(String owner, String repoName) {
        GithubRepo githubRepo = githubClient.getUserRepo(owner, repoName);
        Repo localRepo = repository.getByOwnerAndName(owner, repoName).orElse(new Repo());
        localRepo.update(githubRepo, owner, repoName);
        Repo savedRepo = repository.save(localRepo);
        return repoMapper.toDto(savedRepo);
    }

    @Transactional
    public void deleteRepo(String owner, String repoName) {
        repository.getByOwnerAndName(owner, repoName).orElseThrow(
                () -> new RepositoryNotFoundException(repoName));
        repository.deleteRepositoryByOwnerAndName(owner, repoName);
    }
}