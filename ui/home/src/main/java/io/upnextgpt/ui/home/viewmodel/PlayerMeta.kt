package io.upnextgpt.ui.home.viewmodel

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class PlayerMeta(
    val packageName: String,
    val name: String,
    @DrawableRes
    val iconRes: Int,
    val themeColor: Color,
    val isActive: Boolean,
    val isInstalled: Boolean,
)