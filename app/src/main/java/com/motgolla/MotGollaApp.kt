package com.motgolla

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.motgolla.ui.component.MotGollaScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MotGollaApp() {
    val navController = rememberNavController()
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route
//    val hideBarsRoutes = listOf("splash", "login", "welcome", "signup", "home")
    val currentScreen = MotGollaScreen.fromRoute(currentRoute)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            if (currentScreen?.showTopBar == true) {
                MotGollaTopBar(
                    title = currentScreen.title,
                    showBackButton = currentScreen.showBackButton,
                    onBackClick = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            if (currentScreen?.showBottomBar == true) {
                MotgollaNavBar(navController)
            }
        }
    ) { innerPadding ->
        MotgollaNavHost(
            navController = navController,
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
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
