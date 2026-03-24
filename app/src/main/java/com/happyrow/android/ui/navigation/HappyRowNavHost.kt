package com.happyrow.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.happyrow.android.ui.auth.AuthState

@Composable
fun HappyRowNavHost(
    navController: NavHostController,
    authState: AuthState,
    modifier: Modifier = Modifier
) {
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Route.Home.route
        else -> Route.Welcome.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.Welcome.route) {
            // WelcomeScreen - placeholder
        }
        composable(Route.Login.route) {
            // LoginScreen - placeholder
        }
        composable(Route.Register.route) {
            // RegisterScreen - placeholder
        }
        composable(Route.ForgotPassword.route) {
            // ForgotPasswordScreen - placeholder
        }
        composable(Route.Home.route) {
            // HomeScreen - placeholder
        }
        composable(Route.CreateEvent.route) {
            // CreateEventScreen - placeholder
        }
        composable(
            route = Route.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            // EventDetailScreen(eventId) - placeholder
        }
        composable(Route.Profile.route) {
            // ProfileScreen - placeholder
        }
    }
}
