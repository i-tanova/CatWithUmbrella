package com.tacocat.catwithumbrella.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TextGrey = Color(0xFF96968F)

@Composable
fun CatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(background = Color.Black, surface = Color.Black, onBackground = TextGrey),
        content = content,
    )
}
