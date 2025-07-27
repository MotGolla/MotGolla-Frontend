package com.motgolla

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.motgolla.ui.component.MotGollaTopBar
import com.motgolla.ui.component.MotgollaNavBar
import com.motgolla.ui.navigation.MotgollaNavHost

@Composable
fun MotGollaApp() {
    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route
    val hideBarsRoutes = listOf("splash", "login", "welcome", "signup")

    Scaffold(
        topBar = {
            if (!hideBarsRoutes.contains(currentRoute)) {
                MotGollaTopBar(
                    title = getTitleForRoute(currentRoute),
                    showBackButton = true,
                    onBackClick = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            if (!hideBarsRoutes.contains(currentRoute) || currentRoute=="home") {
                MotgollaNavBar(navController)
            }
        }
    ) { innerPadding ->
        MotgollaNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

//타이틀
fun getTitleForRoute(route: String?): String {
    return when (route) {
        "record" -> "기록"
        "vote" -> "투표"
        "my" -> "내 정보"
        else -> ""
    }
}
