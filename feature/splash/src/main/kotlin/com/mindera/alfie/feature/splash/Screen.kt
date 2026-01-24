package com.mindera.alfie.feature.splash

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object Splash
}