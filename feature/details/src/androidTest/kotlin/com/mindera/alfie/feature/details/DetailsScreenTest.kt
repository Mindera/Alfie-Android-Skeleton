package com.mindera.alfie.feature.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.core.ui.theme.AlfieTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.Modifier

@RunWith(AndroidJUnit4::class)
class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var navigator: AppNavigator
    
    @Before
    fun setup() {
        navigator = mockk(relaxed = true)
    }
    
    @Test
    fun detailsScreen_displaysTitle() {
        composeTestRule.setContent {
            AlfieTheme {
                DetailsScreen(
                    repoId = 1,
                    repoName = "android-skeleton",
                    repoStars = 150,
                    repoUrl = "https://github.com/org/android-skeleton",
                    imageResId = 0,
                    navigator = navigator,
                    modifier = Modifier
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Details")
            .assertIsDisplayed()
    }
    
    @Test
    fun detailsScreen_displaysRepoInfo() {
        composeTestRule.setContent {
            AlfieTheme {
                DetailsScreen(
                    repoId = 1,
                    repoName = "android-skeleton",
                    repoStars = 150,
                    repoUrl = "https://github.com/org/android-skeleton",
                    imageResId = 0,
                    navigator = navigator,
                    modifier = Modifier
                )
            }
        }
        
        // Check repo name is displayed
        composeTestRule
            .onNodeWithText("android-skeleton")
            .assertIsDisplayed()
        
        // Check star count is displayed
        composeTestRule
            .onNodeWithText("150")
            .assertIsDisplayed()
        
        // Check URL is displayed
        composeTestRule
            .onNodeWithText("https://github.com/org/android-skeleton")
            .assertIsDisplayed()
    }
    
    @Test
    fun detailsScreen_backButton_triggersCallback() {
        composeTestRule.setContent {
            AlfieTheme {
                DetailsScreen(
                    repoId = 1,
                    repoName = "android-skeleton",
                    repoStars = 150,
                    repoUrl = "https://github.com/org/android-skeleton",
                    imageResId = 0,
                    navigator = navigator,
                    modifier = Modifier
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()
        
        verify { navigator.navigateBack() }
    }
}
