package com.motgolla.ui.component

sealed class MotGollaScreen(
    val route: String,
    val title: String,
    val showTopBar: Boolean = true,
    val showBottomBar: Boolean = true,
    val showBackButton: Boolean = true
) {
    object Home : MotGollaScreen("home", "홈", showBackButton = false, showTopBar = false)
    object Record : MotGollaScreen("record", "기록")
    object Plus : MotGollaScreen("plus", "등록")
    object Vote : MotGollaScreen("vote", "투표")
    object My : MotGollaScreen("my", "마이")

    // 바텀바에 없는 상세 페이지들
    object RecordDetail : MotGollaScreen("record_detail/{recordId}", "기록 상세")

    companion object {
        fun fromRoute(route: String?): MotGollaScreen? {
            return when {
                route == null -> null
                route == Home.route -> Home
                route == Record.route -> Record
                route == Plus.route -> Plus
                route == Vote.route -> Vote
                route == My.route -> My
                route.startsWith("record_detail") -> RecordDetail
                else -> null
            }
        }
    }
}
