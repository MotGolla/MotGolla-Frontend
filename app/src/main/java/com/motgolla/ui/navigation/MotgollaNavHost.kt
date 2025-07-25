package com.motgolla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.motgolla.ui.screen.home.HomeScreen
import com.motgolla.ui.screen.my.MyScreen
import com.motgolla.ui.screen.record.RecordScreen
import com.motgolla.ui.screen.vote.VoteScreen

@Composable
fun MotgollaNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen() }
        composable("record") { RecordScreen() }
        composable("vote") { VoteScreen() }
        composable("my") { MyScreen() }
    }
}
