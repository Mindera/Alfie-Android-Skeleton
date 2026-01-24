package com.mindera.alfie.feature.details

import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.core.ui.theme.AlfieTheme
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class DetailsScreenSnapshotTest {
    
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )
    
    @Test
    fun detailsScreen_snapshot() {
        val navigator = mockk<AppNavigator>()

        paparazzi.snapshot {
            AlfieTheme {
                DetailsScreen(
                    repoId = 1,
                    repoName = "android-skeleton",
                    repoStars = 150,
                    repoUrl = "",
                    imageResId = 0,
                    navigator = navigator,
                    modifier = Modifier
                )
            }
        }
    }
}
