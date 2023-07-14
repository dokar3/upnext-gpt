package com.dokar.upnextgpt

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.upnextgpt.ui.home.HomeScreen
import io.upnextgpt.ui.home.QueueScreen
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import io.upnextgpt.ui.settings.SettingsScreen
import io.upnextgpt.ui.shared.remember.rememberLifecycleEvent
import org.koin.androidx.compose.koinViewModel

private typealias TransitionScope = AnimatedContentTransitionScope<NavBackStackEntry>

private data class ScreenTransitions(
    val enter: TransitionScope.() -> EnterTransition,
    val exit: TransitionScope.() -> ExitTransition,
)

private val HomeScreenTransitions = ScreenTransitions(
    enter = {
        scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(durationMillis = 300),
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300),
        )
    },
    exit = {
        scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(durationMillis = 225),
        ) + fadeOut(
            animationSpec = tween(durationMillis = 225),
        )
    },
)

private val SubScreenTransitions = ScreenTransitions(
    enter = {
        scaleIn(
            initialScale = 1.2f,
            animationSpec = tween(durationMillis = 300),
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300),
        )
    },
    exit = {
        scaleOut(
            targetScale = 1.2f,
            animationSpec = tween(durationMillis = 225),
        ) + fadeOut(
            animationSpec = tween(durationMillis = 225),
        )
    },
)

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(homeViewModel, lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_STOP) {
            homeViewModel.unobservePlayers()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            homeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
            )

            queueScreen(
                navController = navController,
                homeViewModel = homeViewModel,
            )

            settingsScreen(
                navController = navController,
            )
        }
    }
}

fun NavGraphBuilder.homeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
) {
    composable(
        route = "home",
        exitTransition = HomeScreenTransitions.exit,
        popEnterTransition = HomeScreenTransitions.enter,
    ) {
        HomeScreen(
            viewModel = homeViewModel,
            onNavigate = { navController.navigate(it) },
        )
    }
}

fun NavGraphBuilder.queueScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
) {
    composable(
        route = "queue",
        enterTransition = SubScreenTransitions.enter,
        exitTransition = SubScreenTransitions.exit,
    ) {
        QueueScreen(
            viewModel = homeViewModel,
            onBack = { navController.popBackStack() },
        )
    }
}

fun NavGraphBuilder.settingsScreen(
    navController: NavController,
) {
    composable(
        route = "settings",
        enterTransition = SubScreenTransitions.enter,
        exitTransition = SubScreenTransitions.exit,
    ) {
        SettingsScreen(
            onBack = { navController.popBackStack() },
        )
    }
}