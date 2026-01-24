package com.mindera.alfie.feature.landing

import kotlinx.serialization.Serializable


sealed class Screen {

    @Serializable
    object Landing

}