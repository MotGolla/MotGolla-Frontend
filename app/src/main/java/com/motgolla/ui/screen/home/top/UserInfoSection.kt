package com.motgolla.ui.screen.home.top

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.motgolla.common.RetrofitClient

@Composable
fun UserInfoSection() {
    val context = LocalContext.current

    var nickname by remember { mutableStateOf("닉네임") }
    var profile by remember { mutableStateOf("https://via.placeholder.com/60") }

    // 서버에서 정보 가져오기
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.getAuthService().getMemberInfo()
            if (response.isSuccessful) {
                response.body()?.let { memberInfo ->
                    nickname = memberInfo.name
                    profile = memberInfo.profile?.takeIf { it.isNotBlank() } ?: "https://via.placeholder.com/60"
                }
            } else {
                Log.e("UserInfoSection", "회원 정보 조회 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        Image(
            painter = rememberAsyncImagePainter(profile),
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(20.dp))

        // 사용자명
        Column {
            Text(
                text = "${nickname}님",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "쇼핑을 시작해보세요!",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}
