package com.motgolla.ui.screen.home.bottom

import android.net.Uri
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.*
import com.motgolla.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DepartmentStoreSection(
    navController: NavHostController,
    onClickMore: () -> Unit = {}
) {
    val pagerState = rememberPagerState()

    val images = listOf(
        R.drawable.event1 to "https://www.ehyundai.com/newPortal/EV/EV000003_14_V.do?eventNo=34697&eventCd=B0349900&list_page=&search=&keyword=&eventSearch=&eventSearchDep=&imgLink=/attachfiles/event/1c4232abd4df49f38783b34a28b0600f.png",
        R.drawable.event2 to "https://www.ehyundai.com/newPortal/EV/EV000003_14_V.do?eventNo=28317&eventCd=B0349900&list_page=&search=&keyword=&eventSearch=&eventSearchDep=&imgLink=/attachfiles/event/4d5e2e8816224e6a9ab800bfc065ad83.png",
        R.drawable.event3 to "https://www.ehyundai.com/newPortal/EV/EV000003_14_V.do?eventNo=34698&eventCd=B0349900&list_page=&search=&keyword=&eventSearch=&eventSearchDep=&imgLink=/attachfiles/event/0f8da2287dc844fa9e970c88bfe6a4a1.png",
        R.drawable.event4 to "https://www.ehyundai.com/newPortal/EV/EV000003_14_V.do?eventNo=34657&eventCd=B0349900&list_page=&search=&keyword=&eventSearch=&eventSearchDep=&imgLink=/attachfiles/event/8cef0ad18c354e229551554f82e0b151.png",
        R.drawable.event5 to "https://www.ehyundai.com/newPortal/EV/EV000003_14_V.do?eventNo=34857&eventCd=B0349900&list_page=&search=&keyword=&eventSearch=&eventSearchDep=&imgLink=/attachfiles/event/85f6a28ade674b4f8316d50f7413e80a.png",
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(7000)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(
                nextPage,
                animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
            )
        }
    }

    Column {
        SectionHeader(
            title = "백화점 정보",
            onClickMore = {
                val encoded = Uri.encode("https://www.ehyundai.com/mobile/event/EV/main.do?param=event")
                navController.navigate("webview/$encoded")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(10.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        val encodedUrl = Uri.encode(images[page].second)
                        navController.navigate("webview/$encodedUrl")
                    }
            ) {
                Image(
                    painter = painterResource(id = images[page].first),
                    contentDescription = "이벤트 배너",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            activeColor = Color(0xFF3B40CC),
            inactiveColor = Color.Gray
        )
    }
}
