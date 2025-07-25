package com.motgolla

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.motgolla.ui.component.MotGollaTopBar
import com.motgolla.ui.component.MotgollaNavBar
import com.motgolla.ui.navigation.MotgollaNavHost

@Composable
fun MotGollaApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            MotGollaTopBar(
                title = "쇼핑 기록",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            MotgollaNavBar(navController)
        }
    ) { innerPadding ->
        MotgollaNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
