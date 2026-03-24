package com.happyrow.android.ui.navigation

sealed class Route(val route: String) {
    object Welcome : Route("welcome")
    object Login : Route("login")
    object Register : Route("register")
    object ForgotPassword : Route("forgot-password")
    object Home : Route("home")
    object CreateEvent : Route("create-event")
    object EventDetail : Route("events/{eventId}") {
        fun createRoute(eventId: String) = "events/$eventId"
    }
    object Profile : Route("profile")
}
