package com.motgolla.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MotGollaNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : MotGollaNavScreen("home", "홈", Icons.Filled.Home)
    object Record : MotGollaNavScreen("record", "기록", Icons.Filled.List)
    object Vote : MotGollaNavScreen("vote", "투표", Icons.Filled.CheckCircle)
    object My : MotGollaNavScreen("my", "마이", Icons.Filled.Person)

    companion object {
        val items = listOf(Home, Record, Vote, My)
    }
}
