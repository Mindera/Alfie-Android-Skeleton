package com.mindera.alfie.data.repository

import com.mindera.alfie.networking.GitHubApiService
import com.mindera.alfie.networking.model.GitHubRepo
import com.mindera.alfie.domain.model.Repo
import com.mindera.alfie.domain.repository.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RepoRepository that fetches data from GitHub API.
 * Data layer implementation - no dispatcher logic here (moved to domain layer).
 */
@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService
) : RepoRepository {
    override suspend fun fetchRepos(
        org: String,
        page: Int,
        perPage: Int
    ): Result<List<Repo>> {
        return try {
            val repos = apiService.getOrgRepos(
                org = org,
                perPage = perPage,
                page = page
            )
            Result.success(repos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Maps data model to domain model.
     */
    private fun GitHubRepo.toDomain(): Repo = Repo(
        id = id,
        name = name,
        fullName = fullName,
        url = htmlUrl,
        stars = stargazersCount,
        description = description
    )
}
