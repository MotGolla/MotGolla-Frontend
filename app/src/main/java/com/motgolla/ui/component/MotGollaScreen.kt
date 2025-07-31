package com.motgolla.ui.component

sealed class MotGollaScreen(
    val route: String,
    val title: String,
    val showTopBar: Boolean = true,
    val showBottomBar: Boolean = true,
    val showBackButton: Boolean = true
) {
    // 메인 탭
    object Home : MotGollaScreen("home", "홈", showTopBar = false, showBackButton = false)
    object Record : MotGollaScreen("record", "기록")
    object Plus : MotGollaScreen("plus", "등록")
    object ShoppingRecord : MotGollaScreen("shoppingRecord", "등록")
    object Vote : MotGollaScreen("vote", "투표")
    object My : MotGollaScreen("my", "마이")

    // 기타 상세/예외 화면
    object RecordDetail : MotGollaScreen("record_detail/{recordId}", "기록 상세")
    object Splash : MotGollaScreen(
        "splash",
        "",
        showTopBar = false,
        showBottomBar = false,
        showBackButton = false
    )

    object Login : MotGollaScreen(
        "login",
        "",
        showTopBar = false,
        showBottomBar = false,
        showBackButton = false
    )

    object SignUp : MotGollaScreen(
        "signup",
        "",
        showTopBar = false,
        showBottomBar = false,
        showBackButton = false
    )

    object Welcome : MotGollaScreen(
        "welcome",
        "",
        showTopBar = false,
        showBottomBar = false,
        showBackButton = false
    )

    object VoteProductSelect :
        MotGollaScreen("vote/productSelect", "투표 상품 선택", showBottomBar = false)

    object VoteTitleInput : MotGollaScreen("vote/titleInput", "투표 제목 입력", showBottomBar = false)
    object WebView : MotGollaScreen(
        "webview",
        "",
        showTopBar = false,
        showBottomBar = false,
        showBackButton = true
    )

    companion object {
        fun fromRoute(route: String?): MotGollaScreen? {
            return when {
                route == null -> null
                route == Home.route -> Home
                route == Record.route -> Record
                route == Plus.route -> Plus
                route == ShoppingRecord.route -> ShoppingRecord
                route == Vote.route -> Vote
                route == My.route -> My
                route.startsWith("record_detail") -> RecordDetail
                route == Splash.route -> Splash
                route == Login.route -> Login
                route == SignUp.route -> SignUp
                route == Welcome.route -> Welcome
                route.startsWith(VoteProductSelect.route) -> VoteProductSelect
                route.startsWith(VoteTitleInput.route) -> VoteTitleInput
                route.startsWith(WebView.route) -> WebView
                else -> null
            }
        }
    }
}
