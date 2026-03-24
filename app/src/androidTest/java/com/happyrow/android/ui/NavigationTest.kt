package com.happyrow.android.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.happyrow.android.ui.auth.AuthState
import com.happyrow.android.ui.navigation.HappyRowNavHost
import com.happyrow.android.ui.theme.HappyRowTheme
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun unauthenticated_showsWelcomeScreen() {
        composeTestRule.setContent {
            HappyRowTheme {
                HappyRowNavHost(
                    navController = rememberNavController(),
                    authState = AuthState.Unauthenticated
                )
            }
        }
        composeTestRule.onNodeWithText("HappyRow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
    }
}
