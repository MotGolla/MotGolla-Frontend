package com.motgolla.ui.navigation

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
import com.motgolla.ui.screen.record.ShoppingRecordScreen
import com.motgolla.viewmodel.record.MemoViewModel
import com.motgolla.viewmodel.record.RecordRegisterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.motgolla.ui.screen.record.ImageViewerScreen
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.motgolla.ui.screen.record.RecordDetailScreen
import com.motgolla.ui.screen.record.ShoppingRecordMainScreen
import com.motgolla.ui.screen.vote.VoteProductSelectScreen
import com.motgolla.ui.screen.vote.VoteScreenWrapper
import com.motgolla.ui.screen.vote.VoteTitleInputScreen
import com.motgolla.viewmodel.vote.VoteCreateViewModel
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

    val voteCreateViewModel: VoteCreateViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("vote") { VoteScreenWrapper(navController) }
        composable("my") { MyScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("welcome") { WelcomeScreen(navController) }

        composable(
            route = "vote/productSelect?date={date}",
            arguments = listOf(
                navArgument("date") { defaultValue = "2025-07-30" }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: "2025-07-30"

            VoteProductSelectScreen(
                viewModel = voteCreateViewModel,
                date = date,
                onNext = {
                    navController.navigate("vote/titleInput")
                }
            )
        }
        composable("shoppingRecord") {
            val recordRegisterViewModel: RecordRegisterViewModel = viewModel()
            val memoViewModel: MemoViewModel = viewModel()
            ShoppingRecordScreen(recordRegisterViewModel, memoViewModel, navController = navController)
        }

        composable("record") {
            val recordViewModel: RecordViewModel = viewModel()
            ShoppingRecordMainScreen(navController, recordViewModel)
        }

        composable("plus") {
            val recordRegisterViewModel: RecordRegisterViewModel = viewModel()
            val memoViewModel: MemoViewModel = viewModel()
            ShoppingRecordScreen(recordRegisterViewModel, memoViewModel, navController = navController)
        }

        composable("vote/titleInput") {
            VoteTitleInputScreen(
                viewModel = voteCreateViewModel,
                onSuccess = {
                    navController.navigate("vote") {
                        popUpTo("vote/productSelect") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "image_viewer/{encodedUrls}/{initialIndex}",
            arguments = listOf(
                navArgument("encodedUrls") { type = NavType.StringType },
                navArgument("initialIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("encodedUrls") ?: ""
            val decoded = Uri.decode(encoded)
            val initial = backStackEntry.arguments?.getInt("initialIndex") ?: 0

            ImageViewerScreen(
                navController = navController,
                imageListStr = decoded,
                initialIndex = initial
            )
        }
        composable("record_detail/{recordId}") { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")?.toLong() ?: return@composable
            RecordDetailScreen(recordId = recordId, navController = navController)
        }

    }
}

