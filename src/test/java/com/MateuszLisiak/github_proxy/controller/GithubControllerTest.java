package com.MateuszLisiak.github_proxy.controller;

import com.MateuszLisiak.github_proxy.exception.GithubException;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.service.GithubService;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void getRepo_DataCorrect_RepoDto() throws Exception {
        RepoDto repoDto = new RepoDto("testRepo", null, null, null, null);
        when(githubService.getRepo("testOwner", "testRepo")).thenReturn(repoDto);
        mockMvc.perform(get("/repositories/testOwner/testRepo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("testRepo"));
    }

    @Test
    void getRepo_NotFound_ShouldReturn404() throws Exception {
        when(githubService.getRepo("testOwner", "notFoundRepo"))
                .thenThrow(new GithubException("Not Found", 404));

        mockMvc.perform(get("/repositories/testOwner/notFoundRepo"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getRepo_ServiceUnavailable_ShouldReturn503() throws Exception {
        Request request = Request.create(
                Request.HttpMethod.GET, "/test", Collections.emptyMap(),
                null, StandardCharsets.UTF_8, null
        );
        RetryableException retryException = new RetryableException(
                503, "Service Unavailable", null, null, 0L, request);

        when(githubService.getRepo("testOwner", "testRepo"))
                .thenThrow(retryException);

        mockMvc.perform(get("/repositories/testOwner/testRepo"))
                .andDo(print())
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Service Unavailable"))
                .andExpect(jsonPath("$.status").value(503));
    }
}