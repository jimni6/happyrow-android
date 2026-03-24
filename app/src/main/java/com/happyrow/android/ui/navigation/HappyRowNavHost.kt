package com.happyrow.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.happyrow.android.ui.auth.AuthState
import com.happyrow.android.ui.auth.ForgotPasswordScreen
import com.happyrow.android.ui.auth.LoginScreen
import com.happyrow.android.ui.auth.RegisterScreen
import com.happyrow.android.ui.auth.WelcomeScreen
import com.happyrow.android.ui.events.CreateEventScreen
import com.happyrow.android.ui.events.EventDetailScreen
import com.happyrow.android.ui.home.HomeScreen
import com.happyrow.android.ui.profile.ProfileScreen

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
            WelcomeScreen(navController = navController)
        }
        composable(Route.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Route.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Route.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(Route.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Route.CreateEvent.route,
            arguments = listOf(navArgument("organizerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val organizerId = backStackEntry.arguments?.getString("organizerId") ?: return@composable
            CreateEventScreen(
                navController = navController,
                organizerId = organizerId
            )
        }
        composable(
            route = Route.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            EventDetailScreen(navController = navController)
        }
        composable(Route.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
