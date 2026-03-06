package com.MateuszLisiak.github_proxy.service;

import com.MateuszLisiak.github_proxy.client.GithubClient;
import com.MateuszLisiak.github_proxy.exception.GithubException;
import com.MateuszLisiak.github_proxy.mapper.RepoMapper;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.model.GithubRepo;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GithubServiceTest {
    GithubService githubService;
    GithubClient githubClient;
    RepoMapper repoMapper;


    @BeforeEach
    void setup() {
        this.githubClient = Mockito.mock(GithubClient.class);
        this.repoMapper = Mappers.getMapper(RepoMapper.class);

        this.githubService = new GithubService(
                githubClient,
                repoMapper
        );
    }

    @Test
    void getRepo_dataCorrect_RepoDto() {
        String owner = "testOwner";
        String repo = "testRepo";
        GithubRepo githubRepo = new GithubRepo(repo, null, null, null, null);

        when(githubClient.getUserRepo(owner, repo)).thenReturn(githubRepo);

        RepoDto excepted = repoMapper.toDto(githubRepo);
        RepoDto result = githubService.getRepo(owner, repo);

        assertEquals(excepted, result);
        verify(githubClient).getUserRepo(owner, repo);
    }

    @Test
    void getRepo_serviceUnavailable_RetryableException() {
        String owner = "testOwner";
        String repo = "testRepo";

        Request request = Request.create(
                Request.HttpMethod.GET, "/test", Collections.emptyMap(),
                null, StandardCharsets.UTF_8, null
        );

        RetryableException retryException = new RetryableException(
                503, "Service Unavailable", request.httpMethod(), null, 50L, request
        );

        when(githubClient.getUserRepo(owner, repo)).thenThrow(retryException);

        RetryableException thrown = assertThrows(RetryableException.class,
                () -> githubService.getRepo(owner, repo)
        );
        assertEquals(503, thrown.status());
        verify(githubClient).getUserRepo(owner, repo);
    }

    @Test
    void getRepo_notFound_GithubException() {
        GithubException githubException = new GithubException("Not Found", 404);

        when(githubClient.getUserRepo("testOwner", "notFoundRepo")).thenThrow(githubException);

        GithubException thrown = assertThrows(GithubException.class,
                () -> githubService.getRepo("testOwner", "notFoundRepo")
        );

        assertEquals(404, thrown.getStatus());
        assertEquals("Not Found", thrown.getMessage());
        verify(githubClient).getUserRepo("testOwner", "notFoundRepo");
    }
}
