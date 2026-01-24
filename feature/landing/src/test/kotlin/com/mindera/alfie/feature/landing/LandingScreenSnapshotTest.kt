package com.mindera.alfie.feature.landing

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.core.ui.theme.AlfieTheme
import com.mindera.alfie.domain.model.Repo
import com.mindera.alfie.domain.usecase.GetReposUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LandingScreenSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun landingScreen_loading_snapshot() {
        val getReposUseCase = mockk<GetReposUseCase>(relaxed = true)
        val navigator = mockk<AppNavigator>(relaxed = true)
        // Make it delay forever to capture loading state
        coEvery { getReposUseCase(page = any(), perPage = any()) } coAnswers {
            kotlinx.coroutines.awaitCancellation()
        }
        
        val viewModel = LandingViewModel(getReposUseCase)
        
        paparazzi.snapshot {
            AlfieTheme {
                LandingScreen(
                    navigator = navigator,
                    viewModel = viewModel
                )
            }
        }
    }
    
    @Test
    fun landingScreen_success_snapshot() {
        val getReposUseCase = mockk<GetReposUseCase>(relaxed = true)
        val navigator = mockk<AppNavigator>(relaxed = true)
        val mockRepos = listOf(
            Repo(1, "android-app", "Mindera/android-app", "https://github.com/Mindera/android-app", 150, "Android app"),
            Repo(2, "ios-app", "Mindera/ios-app", "https://github.com/Mindera/ios-app", 120, "iOS app"),
            Repo(3, "web-app", "Mindera/web-app", "https://github.com/Mindera/web-app", 100, "Web app"),
            Repo(4, "backend", "Mindera/backend", "https://github.com/Mindera/backend", 80, "Backend"),
            Repo(5, "frontend", "Mindera/frontend", "https://github.com/Mindera/frontend", 60, "Frontend"),
            Repo(6, "mobile", "Mindera/mobile", "https://github.com/Mindera/mobile", 40, "Mobile")
        )
        coEvery { getReposUseCase(page = any(), perPage = any()) } returns Result.success(mockRepos)
        
        val viewModel = LandingViewModel(getReposUseCase)
        
        // Use test dispatcher to advance time deterministically
        testDispatcher.scheduler.advanceUntilIdle()
        
        paparazzi.snapshot {
            AlfieTheme {
                LandingScreen(
                    navigator = navigator,
                    viewModel = viewModel
                )
            }
        }
    }
}
