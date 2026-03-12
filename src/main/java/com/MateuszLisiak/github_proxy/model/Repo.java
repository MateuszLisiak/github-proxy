package com.MateuszLisiak.github_proxy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Repo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private LocalDateTime createdAt;
    private String owner;
    private String repositoryName;

    public void update(GithubRepo repo, String owner, String repoName) {
        this.fullName = repo.fullName();
        this.description = repo.description();
        this.cloneUrl = repo.cloneUrl();
        this.stars = repo.stars();
        this.createdAt = repo.createdAt();
        this.owner = owner;
        this.repositoryName = repoName;
    }

    public void setOwnerAndRepositoryName(GithubRepo githubRepo) {
        this.owner = getOwnerNameFromFullName(githubRepo.fullName());
        this.repositoryName = getRepositoryNameFromFullName(githubRepo.fullName());
    }

    private String getOwnerNameFromFullName(String fullName) {
        return fullName.split("/")[0];
    }

    private String getRepositoryNameFromFullName(String fullName) {
        return fullName.split("/")[1];
    }
}
