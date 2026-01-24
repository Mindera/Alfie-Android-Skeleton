package com.mindera.alfie.feature.landing

import app.cash.turbine.test
import com.mindera.alfie.domain.model.Repo
import com.mindera.alfie.domain.usecase.GetReposUseCase
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
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LandingViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getReposUseCase: GetReposUseCase
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getReposUseCase = mockk()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        // Setup mock to return repos
        val mockRepos = listOf(
            Repo(1, "repo1", "org/repo1", "https://github.com/org/repo1", 100, "Description 1"),
            Repo(2, "repo2", "org/repo2", "https://github.com/org/repo2", 50, "Description 2")
        )
        coEvery { getReposUseCase(page = any(), perPage = any()) } returns Result.success(mockRepos)
        
        val viewModel = LandingViewModel(getReposUseCase)
        
        // Initial state should be Loading before coroutine completes
        val initialState = viewModel.uiState.value
        assertTrue(initialState is LandingUiState.Loading)
    }
    
    @Test
    fun `repos are loaded successfully`() = runTest(testDispatcher) {
        val mockRepos = listOf(
            Repo(1, "repo1", "org/repo1", "https://github.com/org/repo1", 100, "Description 1"),
            Repo(2, "repo2", "org/repo2", "https://github.com/org/repo2", 50, "Description 2")
        )
        coEvery { getReposUseCase(page = any(), perPage = any()) } returns Result.success(mockRepos)
        
        val viewModel = LandingViewModel(getReposUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is LandingUiState.Success)
        assertEquals(2, state.tiles.size)
        assertEquals("repo1", state.tiles[0].title)
        assertEquals(100, state.tiles[0].stars)
    }
    
    @Test
    fun `error state is set on failure`() = runTest(testDispatcher) {
        coEvery { getReposUseCase(page = any(), perPage = any()) } returns Result.failure(Exception("Network error"))
        
        val viewModel = LandingViewModel(getReposUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is LandingUiState.Error)
        assertEquals("Network error", state.message)
    }

    @Ignore
    @Test
    fun `load more appends repos`() = runTest(testDispatcher) {
        // First page
        val firstPageRepos = listOf(
            Repo(1, "repo1", "org/repo1", "https://github.com/org/repo1", 100, "Description 1")
        )
        // Second page
        val secondPageRepos = listOf(
            Repo(2, "repo2", "org/repo2", "https://github.com/org/repo2", 50, "Description 2")
        )
        
        coEvery { getReposUseCase(page = 1, perPage = any()) } returns Result.success(firstPageRepos)
        coEvery { getReposUseCase(page = 2, perPage = any()) } returns Result.success(secondPageRepos)
        
        val viewModel = LandingViewModel(getReposUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Load more
        viewModel.loadMoreRepos()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is LandingUiState.Success)
        assertEquals(2, state.tiles.size)
    }
}
