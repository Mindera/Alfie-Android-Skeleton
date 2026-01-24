package com.mindera.alfie.skeleton.navigation

import androidx.annotation.DrawableRes
import androidx.navigation.NavHostController
import com.mindera.alfie.core.navigation.AppNavigator
import com.mindera.alfie.feature.landing.Screen.Landing
import com.mindera.alfie.feature.details.Screen.Details
import com.mindera.alfie.feature.splash.Screen.Splash
import javax.inject.Inject

/**
 * Implementation of AppNavigator using Jetpack Navigation Component.
 * This class is part of the :app module and knows about all navigation destinations.
 */
class AppNavigatorImpl @Inject constructor(
    private val navController: NavHostController
) : AppNavigator {
    
    override fun navigateToLanding() {
        navController.navigate(Landing) {
            popUpTo(Splash) { inclusive = true }
        }
    }
    
    override fun navigateToDetails(
        repoId: Int,
        repoName: String,
        repoStars: Int,
        repoUrl: String,
        @DrawableRes imageResId: Int
    ) {
        navController.navigate(
            Details(
                repoId = repoId,
                repoName = repoName,
                repoStars = repoStars,
                repoUrl = repoUrl,
                imageResId = imageResId
            )
        )
    }
    
    override fun navigateBack() {
        navController.popBackStack()
    }
}
