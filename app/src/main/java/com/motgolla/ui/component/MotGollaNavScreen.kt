package com.motgolla.ui.component

import com.motgolla.R

sealed class MotGollaNavScreen(
    val route: String,
    val title: String,
    val iconRes: Int
) {
    object Home : MotGollaNavScreen("home", "홈", R.drawable.home)
    object Record : MotGollaNavScreen("record", "기록", R.drawable.record)
    object Plus : MotGollaNavScreen("plus", "등록", R.drawable.plus)
    object Vote : MotGollaNavScreen("vote", "투표", R.drawable.vote)
    object My : MotGollaNavScreen("my", "마이", R.drawable.my)

    companion object {
        val items = listOf(Home, Record, Plus, Vote, My)
    }
}

