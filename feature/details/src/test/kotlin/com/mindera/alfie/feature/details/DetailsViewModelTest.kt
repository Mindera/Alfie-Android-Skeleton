package com.mindera.alfie.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.mockk.every
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

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Ignore
    @Test
    fun `repo details are correctly loaded from savedStateHandle`() = runTest(testDispatcher) {
        val repoId = 123
        val repoName = "test-repo"
        val repoStars = 456
        val repoUrl = "https://github.com/org/test-repo"
        
        // Create the route object
        val route = Screen.Details(
            repoId = repoId,
            repoName = repoName,
            repoStars = repoStars,
            repoUrl = repoUrl
        )
        
        // Mock SavedStateHandle to return our route when toRoute is called
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.toRoute<Screen.Details>() } returns route
        
        val viewModel = DetailsViewModel(savedStateHandle)
        
        assertEquals(repoId, viewModel.repoId.value)
        assertEquals(repoName, viewModel.repoName.value)
        assertEquals(repoStars, viewModel.repoStars.value)
        assertEquals(repoUrl, viewModel.repoUrl.value)
    }
}
