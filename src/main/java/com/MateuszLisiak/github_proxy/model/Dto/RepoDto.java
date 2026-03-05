package com.MateuszLisiak.github_proxy.model.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepoDto {
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private LocalDateTime createdAt;
}
