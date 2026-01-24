package com.mindera.alfie.domain.usecase

import com.mindera.alfie.domain.model.Repo
import com.mindera.alfie.domain.repository.RepoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetReposUseCaseTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: RepoRepository
    private lateinit var useCase: GetReposUseCase
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        useCase = GetReposUseCase(repository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `invoke returns sorted repos by stars descending`() = runTest(testDispatcher) {
        // Arrange - unsorted repos
        val unsortedRepos = listOf(
            Repo(1, "repo1", "org/repo1", "https://github.com/org/repo1", 50, "Description 1"),
            Repo(2, "repo2", "org/repo2", "https://github.com/org/repo2", 200, "Description 2"),
            Repo(3, "repo3", "org/repo3", "https://github.com/org/repo3", 100, "Description 3")
        )
        coEvery { repository.fetchRepos(any(), any(), any()) } returns Result.success(unsortedRepos)
        
        // Act
        val result = useCase(page = 1, perPage = 10)
        
        // Assert
        assertTrue(result.isSuccess)
        val repos = result.getOrNull()
        assertEquals(3, repos?.size)
        assertEquals(200, repos?.get(0)?.stars) // repo2 with 200 stars
        assertEquals(100, repos?.get(1)?.stars) // repo3 with 100 stars
        assertEquals(50, repos?.get(2)?.stars)  // repo1 with 50 stars
    }
    
    @Test
    fun `invoke propagates repository errors`() = runTest(testDispatcher) {
        // Arrange
        val exception = Exception("Network error")
        coEvery { repository.fetchRepos(any(), any(), any()) } returns Result.failure(exception)
        
        // Act
        val result = useCase(page = 1, perPage = 10)
        
        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
    
    @Test
    fun `invoke uses correct default parameters`() = runTest(testDispatcher) {
        // Arrange
        val repos = listOf(
            Repo(1, "repo1", "org/repo1", "https://github.com/org/repo1", 100, "Description")
        )
        coEvery { repository.fetchRepos("Mindera", 1, 10) } returns Result.success(repos)
        
        // Act
        val result = useCase()
        
        // Assert
        assertTrue(result.isSuccess)
    }
}
