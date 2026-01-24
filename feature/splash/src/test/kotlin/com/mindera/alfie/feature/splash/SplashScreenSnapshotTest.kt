package com.mindera.alfie.feature.splash

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.core.ui.theme.AlfieTheme
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class SplashScreenSnapshotTest {
    
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )
    
    @Test
    fun splashScreen_snapshot() {
        val viewModel = SplashViewModel()
        val navigator = mockk<AppNavigator>(relaxed = true)

        paparazzi.snapshot {
            AlfieTheme {
                SplashScreen(
                    navigator = navigator,
                    viewModel = viewModel
                )
            }
        }
    }
}
