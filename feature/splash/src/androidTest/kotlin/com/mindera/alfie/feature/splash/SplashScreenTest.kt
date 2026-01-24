package com.mindera.alfie.feature.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.core.ui.theme.AlfieTheme
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    lateinit var viewModel : SplashViewModel
    lateinit var navigator: AppNavigator
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Before
    fun setup() {
        viewModel = SplashViewModel()
        navigator = mockk(relaxed = true)
    }
    
    @Test
    fun splashScreen_displaysAlfieText() {
        composeTestRule.setContent {
            AlfieTheme {
                SplashScreen(navigator = navigator, viewModel = viewModel)
            }
        }
        
        composeTestRule
            .onNodeWithText("ALFIE")
            .assertIsDisplayed()
    }
    
    @Test
    fun splashScreen_displaysLoadingIndicator() {
        composeTestRule.setContent {
            AlfieTheme {
                SplashScreen(navigator = navigator, viewModel = viewModel)
            }
        }
        
        // Check that the loading indicator is displayed
        composeTestRule
            .onNodeWithTag("loadingIndicator")
            .assertIsDisplayed()
    }
}
