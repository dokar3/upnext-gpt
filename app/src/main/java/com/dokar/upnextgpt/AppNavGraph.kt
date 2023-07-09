package com.dokar.upnextgpt

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.upnextgpt.ui.home.HomeScreen
import io.upnextgpt.ui.home.QueueScreen
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable(
                route = "home",
                exitTransition = {
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(durationMillis = 225),
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 225),
                    )
                },
                popEnterTransition = {
                    scaleIn(initialScale = 0.8f) + fadeIn()
                },
            ) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigate = { navController.navigate(it) },
                )
            }

            composable(
                route = "queue",
                enterTransition = {
                    scaleIn(
                        initialScale = 1.2f,
                        animationSpec = tween(durationMillis = 300),
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 300),
                    )
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 1.2f,
                        animationSpec = tween(durationMillis = 225),
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 225),
                    )
                },
            ) {
                QueueScreen(
                    viewModel = homeViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}