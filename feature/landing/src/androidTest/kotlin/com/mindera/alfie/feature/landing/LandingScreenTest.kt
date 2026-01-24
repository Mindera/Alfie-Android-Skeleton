package com.mindera.alfie.feature.landing

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.core.ui.theme.AlfieTheme
import com.mindera.alfie.domain.model.Repo
import com.mindera.alfie.domain.usecase.GetReposUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenTest {

    private lateinit var getReposUseCase: GetReposUseCase
    private lateinit var viewModel: LandingViewModel
    private lateinit var navigator: AppNavigator
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Before
    fun setup() {
        getReposUseCase = mockk(relaxed = true)
        navigator = mockk(relaxed = true)
        
        // Setup default mock response
        val mockRepos = listOf(
            Repo(1, "repo1", "org/repo1", "https://github.com/org/repo1", 100, "Description 1"),
            Repo(2, "repo2", "org/repo2", "https://github.com/org/repo2", 50, "Description 2")
        )
        coEvery { getReposUseCase(page = any(), perPage = any()) } returns Result.success(mockRepos)
        
        viewModel = LandingViewModel(getReposUseCase)
    }
    
    @Test
    fun landingScreen_displaysTitle() {
        composeTestRule.setContent {
            AlfieTheme {
                LandingScreen(navigator = navigator, viewModel = viewModel)
            }
        }
        
        composeTestRule
            .onNodeWithText("Alfie Skeleton")
            .assertIsDisplayed()
    }
    
    @Test
    fun landingScreen_displaysRepoTiles() {
        composeTestRule.setContent {
            AlfieTheme {
                LandingScreen(navigator = navigator, viewModel = viewModel)
            }
        }
        
        // Wait for loading to complete
        composeTestRule.waitForIdle()
        
        // Check that repo names are displayed
        composeTestRule
            .onNodeWithText("repo1")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("repo2")
            .assertIsDisplayed()
    }
    
    @Test
    fun landingScreen_tileClick_triggersNavigation() {
        composeTestRule.setContent {
            AlfieTheme {
                LandingScreen(navigator = navigator, viewModel = viewModel)
            }
        }
        
        composeTestRule.waitForIdle()
        
        composeTestRule
            .onNodeWithText("repo1")
            .performClick()
        
        verify { navigator.navigateToDetails(any(), any(), any(), any(), any()) }
    }
}
