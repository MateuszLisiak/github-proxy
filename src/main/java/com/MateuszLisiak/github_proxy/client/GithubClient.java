package com.MateuszLisiak.github_proxy.client;

import com.MateuszLisiak.github_proxy.configuration.FeignConfiguration;
import com.MateuszLisiak.github_proxy.model.GithubRepo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "github-proxy", url = "${spring.cloud.openfeign.client.config.github-proxy.url}",
        configuration = FeignConfiguration.class)
public interface GithubClient {
    @GetMapping("/repos/{owner}/{repo}")
    GithubRepo getUserRepo(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}