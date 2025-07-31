package com.motgolla.ui.screen.login

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.motgolla.R
import com.motgolla.domain.login.api.service.KakaoLoginService
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.motgolla.ui.theme.KakaoSans

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val imageList = listOf(R.drawable.introduce1, R.drawable.introduce2)
        val currentImageIndex = remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(2000) // 2초마다 이미지 전환
                currentImageIndex.value = (currentImageIndex.value + 1) % imageList.size
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Crossfade(targetState = currentImageIndex.value) { index ->
                Image(
                    painter = painterResource(id = imageList[index]),
                    contentDescription = "Intro Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }

        // 카카오 로그인 버튼
        Image(
            painter = painterResource(id = R.drawable.kakaologin),
            contentDescription = "Kakao Login Button",
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent)
                .clickable {
                    KakaoLoginService.loginWithKakao(
                        context = context,
                        onSuccess = { nickname, profileImageUrl, oauthId, idToken ->
                            if (nickname != null) {
                                KakaoLoginService.handleKakaoLoginSuccess(
                                    context,
                                    navController,
                                    nickname,
                                    profileImageUrl,
                                    oauthId,
                                    idToken
                                )
                            }
                        },
                        onFailure = { error ->
                            Log.e("KakaoLogin", "로그인 실패: $error")
                        }
                    )
                }
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 일반 로그인 버튼
        Button(
            onClick = { 
                navController.navigate("normalLogin")
            },
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(
                text = "일반 로그인",
                color = Color.DarkGray,
                fontSize = 19.sp,
                fontFamily = KakaoSans,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // You can pass a dummy NavController using rememberNavController for preview
    LoginScreen(navController = rememberNavController())
}