package com.MateuszLisiak.github_proxy.mapper;

import com.MateuszLisiak.github_proxy.model.Repo;
import com.MateuszLisiak.github_proxy.model.dto.RepoDto;
import com.MateuszLisiak.github_proxy.model.GithubRepo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepoMapper {
    RepoDto toDto(GithubRepo githubRepo);

    Repo toEntity(GithubRepo githubRepo);

    RepoDto toDto(Repo repo);
}
