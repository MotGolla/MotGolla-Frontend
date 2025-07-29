package com.motgolla

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
    val hideBarsRoutes = listOf("splash", "login", "welcome", "signup", "home")

    val shouldHideNavBar =
                currentRoute?.startsWith("vote/productSelect") == true ||
                currentRoute?.startsWith("vote/titleInput") == true

    Scaffold(
        containerColor = Color.White,
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
            if (!shouldHideNavBar && !hideBarsRoutes.contains(currentRoute) && !currentRoute.orEmpty().startsWith("webview") ||
                currentRoute == "home"
            ) {
                MotgollaNavBar(navController)
            }
        }
    ) { innerPadding ->
        MotgollaNavHost(
            navController = navController,
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 0.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
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
