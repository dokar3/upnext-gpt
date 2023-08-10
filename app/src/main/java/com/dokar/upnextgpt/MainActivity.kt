package com.dokar.upnextgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dokar.upnextgpt.ui.theme.UpNextGPTTheme
import io.upnextgpt.data.settings.Settings
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settings: Settings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDynamicColorEnabled by settings
                .dynamicColorEnabledFlow
                .collectAsState(initial = false)

            UpNextGPTTheme(
                darkTheme = true,
                dynamicColor = isDynamicColorEnabled,
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}
