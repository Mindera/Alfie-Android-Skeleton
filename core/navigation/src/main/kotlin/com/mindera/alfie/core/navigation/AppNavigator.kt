package com.mindera.alfie.core.navigation

import androidx.annotation.DrawableRes

/**
 * Navigation interface that defines all possible navigation actions in the app.
 * Feature modules depend on this interface, not on the implementation.
 * The :app module provides the concrete implementation.
 */
interface AppNavigator {
    /**
     * Navigate to the landing screen
     */
    fun navigateToLanding()
    
    /**
     * Navigate to the details screen
     * @param repoId Repository ID
     * @param repoName Repository name
     * @param repoStars Number of stars
     * @param repoUrl Repository URL
     * @param imageResId Optional drawable resource ID
     */
    fun navigateToDetails(
        repoId: Int,
        repoName: String,
        repoStars: Int,
        repoUrl: String,
        @DrawableRes imageResId: Int = 0
    )
    
    /**
     * Navigate back to the previous screen
     */
    fun navigateBack()
}
