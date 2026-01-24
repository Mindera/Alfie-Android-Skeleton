package com.mindera.alfie.domain.repository

import com.mindera.alfie.domain.model.Repo

/**
 * Repository interface for accessing repository data.
 * This abstraction decouples the domain layer from data layer implementation details.
 */
interface RepoRepository {
    /**
     * Fetches repositories for a given organization.
     *
     * @param org Organization name
     * @param page Page number for pagination
     * @param perPage Number of items per page
     * @return Result containing list of repositories or an error
     */
    suspend fun fetchRepos(
        org: String = "Mindera",
        page: Int,
        perPage: Int = 10
    ): Result<List<Repo>>
}
