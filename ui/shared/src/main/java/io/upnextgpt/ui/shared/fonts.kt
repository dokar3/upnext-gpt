package io.upnextgpt.ui.shared

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val FontFamily.Companion.Jost: FontFamily by lazy {
    FontFamily(
        Font(R.font.jost_thin, FontWeight.Thin),
        Font(R.font.jost_regular, FontWeight.Medium),
        Font(R.font.jost_bold, FontWeight.Bold),
    )
}