package com.happyrow.android.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.happyrow.android.ui.auth.WelcomeScreen
import com.happyrow.android.ui.theme.HappyRowTheme
import org.junit.Rule
import org.junit.Test

class AuthScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeScreen_displaysButtons() {
        composeTestRule.setContent {
            HappyRowTheme {
                WelcomeScreen(navController = rememberNavController())
            }
        }
        composeTestRule.onNodeWithText("HappyRow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_createAccountButtonClickable() {
        composeTestRule.setContent {
            HappyRowTheme {
                WelcomeScreen(navController = rememberNavController())
            }
        }
        composeTestRule.onNodeWithText("Create Account").performClick()
    }

    @Test
    fun welcomeScreen_signInButtonClickable() {
        composeTestRule.setContent {
            HappyRowTheme {
                WelcomeScreen(navController = rememberNavController())
            }
        }
        composeTestRule.onNodeWithText("Sign In").performClick()
    }
}
