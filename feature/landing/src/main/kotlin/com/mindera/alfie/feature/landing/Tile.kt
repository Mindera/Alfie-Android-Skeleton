package com.mindera.alfie.feature.landing

import androidx.annotation.DrawableRes

data class Tile(
    val id: Int,
    val title: String,
    val imageUrl: String? = null,
    val stars: Int = 0,
    val url: String = "",
    @DrawableRes val imageResId: Int = 0
)
