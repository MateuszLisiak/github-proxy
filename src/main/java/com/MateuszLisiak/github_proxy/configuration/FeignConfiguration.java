package com.MateuszLisiak.github_proxy.configuration;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfiguration {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new GithubErrorDecoder();
    }
}