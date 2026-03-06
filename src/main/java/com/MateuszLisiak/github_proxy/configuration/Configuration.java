package com.MateuszLisiak.github_proxy.configuration;

import com.MateuszLisiak.github_proxy.Custom5xxErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class Configuration {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new Custom5xxErrorDecoder();
    }
}
