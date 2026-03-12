package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.exception.RepositoryAlreadyExistsException;
import com.MateuszLisiak.github_proxy.exception.RepositoryNotFoundException;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GithubService githubService;

    @Test
    void getGithubRepo_Success_ShouldReturnRepoDto() throws Exception {
        RepoDto repoDto = new RepoDto("testRepo", "desc", null, 10, null);
        when(githubService.getGithubRepo("testOwner", "testRepo")).thenReturn(repoDto);
        mockMvc.perform(get("/repositories/testOwner/testRepo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("testRepo"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.stars").value(10));
    }

    @Test
    void getLocalRepo_Success_ShouldReturnRepoDto() throws Exception {
        RepoDto repoDto = new RepoDto("testRepo", "desc", null, 10, null);
        when(githubService.getLocalRepo("testOwner", "testRepo")).thenReturn(repoDto);
        mockMvc.perform(get("/local/repositories/testOwner/testRepo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("testRepo"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.stars").value(10));
    }

    @Test
    void getLocalRepo_NotFound_ShouldReturn404() throws Exception {
        String owner = "testOwner";
        String repoName = "notFoundRepo";
        when(githubService.getLocalRepo(owner, repoName))
                .thenThrow(new RepositoryNotFoundException(repoName));
        mockMvc.perform(get("/local/repositories/testOwner/notFoundRepo"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void putRepo_Success_ShouldReturn200() throws Exception {
        RepoDto repoDto = new RepoDto("repoName", "descUpdated", null, 10, null);
        when(githubService.putRepo("owner", "repoName")).thenReturn(repoDto);
        mockMvc.perform(put("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("repoName"))
                .andExpect(jsonPath("$.description").value("descUpdated"))
                .andExpect(jsonPath("$.stars").value(10));
    }

    @Test
    void deleteRepo_Success_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteRepo_NotFound_ShouldReturn404() throws Exception {
        doThrow(new RepositoryNotFoundException("repoName"))
                .when(githubService).deleteRepo("owner", "repoName");
        mockMvc.perform(delete("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void postRepo_Success_ShouldReturn201() throws Exception {
        RepoDto repoDto = new RepoDto("repoName", "desc", null, 10, null);
        when(githubService.postGithubRepo("owner", "repoName")).thenReturn(repoDto);
        mockMvc.perform(post("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("repoName"));
    }

    @Test
    void postRepo_AlreadyExists_ShouldReturn409() throws Exception {
        when(githubService.postGithubRepo("owner", "repoName"))
                .thenThrow(new RepositoryAlreadyExistsException("owner", "repoName"));
        mockMvc.perform(post("/repositories/owner/repoName"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Repository 'owner/repoName' already exists"));
    }
}