package com.MateuszLisiak.github_proxy.repository;

import com.MateuszLisiak.github_proxy.model.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoRepository extends JpaRepository<Repo, String> {
    Optional<Repo> getByOwnerAndRepoName(String owner, String fullName);

    void deleteRepositoryByOwnerAndRepoName(String owner, String repoName);

    boolean existsByOwnerAndRepoName(String owner, String repoName);
}