package com.mindera.alfie.skeleton.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mindera.alfie.feature.details.DetailsScreen
import com.mindera.alfie.feature.landing.LandingScreen
import com.mindera.alfie.feature.splash.Screen.Splash
import com.mindera.alfie.feature.landing.Screen.Landing
import com.mindera.alfie.feature.details.Screen.Details
import com.mindera.alfie.feature.splash.SplashScreen

@Composable
fun AlfieNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    intent: Intent? = null
) {
    // Create navigator instance
    val navigator = AppNavigatorImpl(navController)
    
    LaunchedEffect(intent) {
        intent?.data?.let { uri ->
            if (uri.scheme == "alfie" && uri.host == "landing.page") {
                navigator.navigateToLanding()
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = Splash,
        modifier = modifier
    ) {
        composable<Splash> {
            SplashScreen(navigator = navigator)
        }
        
        composable<Landing> {
            LandingScreen(navigator = navigator)
        }
        
        composable<Details> { backStackEntry ->
            val details = backStackEntry.toRoute<Details>()
            DetailsScreen(
                repoId = details.repoId,
                repoName = details.repoName,
                repoStars = details.repoStars,
                repoUrl = details.repoUrl,
                imageResId = details.imageResId,
                navigator = navigator
            )
        }
    }
}
