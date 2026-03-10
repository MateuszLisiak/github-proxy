package com.MateuszLisiak.github_proxy.service;

import com.MateuszLisiak.github_proxy.client.GithubClient;
import com.MateuszLisiak.github_proxy.exception.RepositoryAlreadyExistsException;
import com.MateuszLisiak.github_proxy.exception.RepositoryNotFoundException;
import com.MateuszLisiak.github_proxy.mapper.RepoMapper;
import com.MateuszLisiak.github_proxy.model.Repo;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.model.GithubRepo;
import com.MateuszLisiak.github_proxy.repository.RepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GithubServiceTest {
    GithubService githubService;
    GithubClient githubClient;
    RepoMapper repoMapper;
    RepoRepository repoRepository;


    @BeforeEach
    void setup() {
        this.githubClient = Mockito.mock(GithubClient.class);
        this.repoMapper = Mappers.getMapper(RepoMapper.class);
        this.repoRepository = Mockito.mock(RepoRepository.class);

        this.githubService = new GithubService(
                githubClient,
                repoMapper,
                repoRepository
        );
    }

    @Test
    void getGithubRepo_Success_ShouldReturnRepoDto() {
        String owner = "testOwner";
        String repoName = "testRepo";
        GithubRepo githubRepo = new GithubRepo(owner + "/" + repoName, "test", null,
                null, null);
        when(githubClient.getUserRepo(owner, repoName)).thenReturn(githubRepo);
        RepoDto result = githubService.getGithubRepo(owner, repoName);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("testOwner/testRepo", result.fullName()),
                () -> assertEquals("test", result.description())
        );
        verify(githubClient).getUserRepo(owner, repoName);
        verifyNoMoreInteractions(githubClient);
    }

    @Test
    void getLocalRepo_Success_ShouldReturnRepoDto() {
        String owner = "testOwner";
        String repoName = "testRepo";
        Repo repo = new Repo();
        repo.setFullName(owner + "/" + repoName);
        when(repoRepository.getByOwnerAndName(owner, repoName)).thenReturn(Optional.of(repo));
        RepoDto result = githubService.getLocalRepo(owner, repoName);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(owner + "/" + repoName, result.fullName())
        );
        verify(repoRepository).getByOwnerAndName(owner, repoName);
        verifyNoMoreInteractions(repoRepository);
    }

    @Test
    void getLocalRepo_NotFound_ShouldThrowRepositoryNotFoundException() {
        String owner = "testOwner";
        String repoName = "testRepo";

        when(repoRepository.getByOwnerAndName(owner, repoName)).thenReturn(Optional.empty());

        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class,
                () -> githubService.getLocalRepo(owner, repoName)
        );
        assertAll(
                () -> assertEquals(404, exception.getStatus()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus()),
                () -> assertEquals("Repository with name 'testRepo' not found", exception.getMessage())
        );
        verify(repoRepository).getByOwnerAndName(owner, repoName);
        verifyNoMoreInteractions(repoRepository);
    }

    @Test
    void postGithubRepo_Success_ShouldSaveAndReturnRepoDto() {
        GithubRepo githubRepo = new GithubRepo("owner/repoName", "desc", null, null, null);
        when(repoRepository.existsByOwnerAndName("owner", "repoName")).thenReturn(false);
        when(githubClient.getUserRepo("owner", "repoName")).thenReturn(githubRepo);
        when(repoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        RepoDto result = githubService.postGithubRepo("owner", "repoName");
        assertNotNull(result);
        verify(repoRepository).existsByOwnerAndName("owner", "repoName");
        verify(githubClient).getUserRepo("owner", "repoName");
        verify(repoRepository).save(any());
    }

    @Test
    void postGithubRepo_AlreadyExists_ShouldThrowRepositoryAlreadyExistsException() {
        when(repoRepository.existsByOwnerAndName("owner", "repoName")).thenReturn(true);
        RepositoryAlreadyExistsException exception = assertThrows(RepositoryAlreadyExistsException.class,
                () -> githubService.postGithubRepo("owner", "repoName"));
        assertAll(
                () -> assertEquals(409, exception.getStatus()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus()),
                () -> assertEquals("Repository 'owner/repoName' already exists", exception.getMessage())
        );
        verifyNoInteractions(githubClient);
    }

    @Test
    void putRepo_Success_ShouldUpdateAndReturnRepoDto() {
        Repo repo = new Repo();
        GithubRepo githubRepo = new GithubRepo("owner/repoName", "updatedDesc", null, null, null);
        when(repoRepository.getByOwnerAndName("owner", "repoName")).thenReturn(Optional.of(repo));
        when(githubClient.getUserRepo("owner", "repoName")).thenReturn(githubRepo);
        when(repoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        RepoDto result = githubService.putRepo("owner", "repoName");
        assertNotNull(result);
        verify(repoRepository).getByOwnerAndName("owner", "repoName");
        verify(githubClient).getUserRepo("owner", "repoName");
        verify(repoRepository).save(repo);
    }

    @Test
    void deleteRepo_Success_ShouldCallDelete() {
        Repo repo = new Repo();
        when(repoRepository.getByOwnerAndName("owner", "repoName")).thenReturn(Optional.of(repo));
        githubService.deleteRepo("owner", "repoName");
        verify(repoRepository).getByOwnerAndName("owner", "repoName");
        verify(repoRepository).deleteRepositoryByOwnerAndName("owner", "repoName");
    }

    @Test
    void deleteRepo_NotFound_ShouldThrowRepositoryNotFoundException() {
        when(repoRepository.getByOwnerAndName("owner", "repoName")).thenReturn(Optional.empty());
        RepositoryNotFoundException exception = assertThrows(RepositoryNotFoundException.class,
                () -> githubService.deleteRepo("owner", "repoName"));
        assertAll(
                () -> assertEquals(404, exception.getStatus()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus()),
                () -> assertEquals("Repository with name 'repoName' not found", exception.getMessage())
        );
    }
}
