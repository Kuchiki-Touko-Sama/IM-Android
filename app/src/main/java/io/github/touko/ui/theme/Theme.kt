package io.github.touko.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

object ThemeManager {
    private const val DARK_MODE_KEY = "dark_mode"
    private const val DYNAMIC_COLOR_KEY = "dynamic_color"

    var darkMode by mutableStateOf(false)

    var dynamicColor by mutableStateOf(true)

    init {
        val mmkv = com.tencent.mmkv.MMKV.defaultMMKV()
        darkMode = mmkv.decodeBool(DARK_MODE_KEY, false)
        dynamicColor = mmkv.decodeBool(DYNAMIC_COLOR_KEY, true)
    }

    fun changeDarkMode(dark: Boolean) {
        darkMode = dark
        com.tencent.mmkv.MMKV.defaultMMKV().encode(DARK_MODE_KEY, dark)
    }

    fun changeDynamicColor(dynamic: Boolean) {
        dynamicColor = dynamic
        com.tencent.mmkv.MMKV.defaultMMKV().encode(DYNAMIC_COLOR_KEY, dynamic)
    }
}

@Composable
fun Im_android_appTheme(
    darkTheme: Boolean = ThemeManager.darkMode,
    dynamicColor: Boolean = ThemeManager.dynamicColor,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme)
                dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}