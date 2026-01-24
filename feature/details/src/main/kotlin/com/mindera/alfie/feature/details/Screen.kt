package com.mindera.alfie.feature.details

import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data class Details(
        val repoId: Int,
        val repoName: String,
        val repoStars: Int,
        val repoUrl: String,
        @DrawableRes val imageResId: Int = 0
    )

}