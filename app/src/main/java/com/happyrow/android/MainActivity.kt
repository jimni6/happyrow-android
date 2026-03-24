package com.happyrow.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.happyrow.android.ui.auth.AuthState
import com.happyrow.android.ui.auth.AuthViewModel
import com.happyrow.android.ui.navigation.HappyRowNavHost
import com.happyrow.android.ui.theme.HappyRowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappyRowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val authState by authViewModel.authState.collectAsStateWithLifecycle()
                    val navController = rememberNavController()

                    if (authState !is AuthState.Loading) {
                        HappyRowNavHost(
                            navController = navController,
                            authState = authState
                        )
                    }
                }
            }
        }
    }
}
