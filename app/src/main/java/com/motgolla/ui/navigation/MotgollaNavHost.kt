package com.motgolla.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.motgolla.common.storage.TokenManager
import com.motgolla.ui.screen.SplashScreen
import com.motgolla.ui.screen.home.HomeScreen
import com.motgolla.ui.screen.login.LoginScreen
import com.motgolla.ui.screen.login.SignUpScreen
import com.motgolla.ui.screen.login.WelcomeScreen
import com.motgolla.ui.screen.my.MyScreen
import com.motgolla.ui.screen.record.ShoppingRecordMainScreen
import com.motgolla.ui.screen.record.ShoppingRecordScreen
import com.motgolla.ui.screen.vote.VoteScreen
import com.motgolla.viewmodel.record.MemoViewModel
import com.motgolla.viewmodel.record.RecordRegisterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motgolla.viewmodel.record.RecordViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MotgollaNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    // Listen for token expiration events and navigate to login
    LaunchedEffect(Unit) {
        TokenManager.tokenErrorFlow.collect {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("home") { HomeScreen() }
        composable("record") {
            val recordViewModel: RecordViewModel = viewModel()
            ShoppingRecordMainScreen(navController,recordViewModel)
        }

        composable("vote") { VoteScreen() }
        composable("my") { MyScreen() }
        composable("login") {
            LoginScreen(navController)
        }
        // ShoppingRecordScreen 등록
        composable("shoppingRecord") {
            val recordRegisterViewModel: RecordRegisterViewModel = viewModel()
            val memoViewModel: MemoViewModel = viewModel()
            ShoppingRecordScreen(viewModel = recordRegisterViewModel, memoViewModel = memoViewModel)
        }
        composable("signup/{idToken}/{oauthId}/{nickname}") { backStackEntry ->
            val idToken = backStackEntry.arguments?.getString("idToken") ?: ""
            val oauthId = backStackEntry.arguments?.getString("oauthId") ?: ""
            val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
            SignUpScreen(navController, idToken, oauthId, nickname)
        }
        composable("welcome") {
            WelcomeScreen(navController)
        }
    }
}

