package com.MateuszLisiak.github_proxy.client;

import com.MateuszLisiak.github_proxy.exception.GithubException;
import com.MateuszLisiak.github_proxy.model.GithubRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureWireMock(port = 8888)
public class GithubClientTest {
    @Autowired
    GithubClient client;
    @Autowired
    WireMockServer wireMockServer;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getUserRepo_Success_ShouldReturnGithubRepo() throws JsonProcessingException {
        //given
        GithubRepo response = new GithubRepo("owner/repo", "desc", null, 1,
                LocalDateTime.of(1999, 4, 4, 12, 20)
        );
        wireMockServer.stubFor(get("/repos/owner/repo").willReturn(
                aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(response))
                        .withStatus(200)
        ));
        //when
        GithubRepo result = client.getUserRepo("owner", "repo");
        //then
        assertAll(
                () -> assertEquals("owner/repo", result.fullName()),
                () -> assertEquals("desc", result.description()),
                () -> assertEquals(1, result.stars()),
                () -> assertEquals(LocalDateTime.of(1999, 4, 4, 12, 20), result.createdAt())
        );
    }

    @Test
    void getUserRepo_NotFound_ShouldThrowGithubException() {
        //given
        wireMockServer.stubFor(get("/repos/owner/name")
                .willReturn(aResponse().withStatus(404)));
        //when
        GithubException exception = assertThrows(
                GithubException.class,
                () -> client.getUserRepo("owner", "name")
        );
        //then
        assertAll(
                () -> assertEquals(404, exception.getStatus()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus())
        );
    }

    @Test
    void getUserRepo_ServiceUnavailable_ShouldThrowRetryableException() {
        wireMockServer.stubFor(get("/repos/owner/name")
                .willReturn(aResponse().withStatus(503)));
        RetryableException exception = assertThrows(RetryableException.class,
                () -> client.getUserRepo("owner", "name"));
        assertAll(
                () -> assertEquals(503, exception.status())
        );
    }

    @Test
    void getUserRepo_ServerError_ShouldThrowGithubException() {
        //given
        wireMockServer.stubFor(get("/repos/owner/name")
                .willReturn(aResponse().withStatus(500)));
        //when
        GithubException exception = assertThrows(
                GithubException.class,
                () -> client.getUserRepo("owner", "name")
        );
        //then
        assertAll(
                () -> assertEquals(500, exception.getStatus()),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus())
        );
    }
}