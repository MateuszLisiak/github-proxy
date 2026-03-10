package com.MateuszLisiak.github_proxy.configuration;

import com.MateuszLisiak.github_proxy.exception.GithubException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class Custom5xxErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = feign.FeignException.errorStatus(methodKey, response);
        int statusCode = response.status();
        if (statusCode == 503) {
            return new RetryableException(
                    response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    50L,
                    response.request());
        }
        return new GithubException(exception.getMessage(), statusCode, HttpStatus.valueOf(statusCode));
    }
}
