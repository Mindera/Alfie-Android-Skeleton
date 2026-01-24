package com.mindera.alfie.domain.usecase

import com.mindera.alfie.domain.model.Repo
import com.mindera.alfie.domain.repository.RepoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for fetching repositories.
 * Encapsulates business logic and orchestrates data access.
 * Changes coroutine context to IO dispatcher as soon as possible.
 */
class GetReposUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    /**
     * Executes the use case to fetch repositories.
     * Switches to IO dispatcher immediately to avoid blocking main thread.
     *
     * @param org Organization name
     * @param page Page number
     * @param perPage Items per page
     * @return Result containing sorted list of repositories
     */
    suspend operator fun invoke(
        org: String = "Mindera",
        page: Int = 1,
        perPage: Int = 10
    ): Result<List<Repo>> = withContext(Dispatchers.IO) {
        repository.fetchRepos(org, page, perPage)
            .map { repos ->
                // Business logic: Sort by stars in descending order
                repos.sortedByDescending { it.stars }
            }
    }
}
