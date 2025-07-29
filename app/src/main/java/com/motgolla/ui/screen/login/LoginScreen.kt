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
import com.motgolla.R
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.login.api.service.KakaoLoginService
import com.motgolla.domain.login.data.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

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
                        onSuccess = { nickname, profileUrl, oauthId, idToken ->
                            Log.d(
                                "KakaoLogin",
                                "카카오 로그인 성공: $nickname, $profileUrl, $oauthId, $idToken"
                            )

                            val loginRequest = LoginRequest(idToken = idToken!!, oauthId = oauthId)
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    // 서버 로그인 요청
                                    val response =
                                        RetrofitClient.getAuthService().login(loginRequest)
                                    withContext(Dispatchers.Main) {
                                        // 가입된 회원인 경우 welcome 페이지로 이동
                                        if (response.isSuccessful) {
                                            Log.d("Login", "로그인 성공")
                                            // 토큰 저장
                                            val tokenResponse = response.body()
                                            if (tokenResponse != null) {
                                                TokenStorage.save(context, tokenResponse)
                                            } else {
                                                Log.e("SignUp", "token is null")
                                            }
                                            navController.navigate("welcome")

                                            // 아닌 경우 회원 가입 페이지로 이동
                                        } else if (response.code() == 401) {
                                            Log.d("Login", "회원 가입 필요: ${response.body()}")
                                            navController.navigate("signup/${idToken}/${oauthId}/${nickname}")
                                        } else {
                                            Log.e(
                                                "Login",
                                                "Unexpected response: ${response.code()}"
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("Login", "로그인 요청 실패", e)
                                }
                            }
                        },
                        onFailure = { error ->
                            Log.e("KakaoLogin", "로그인 실패: $error")
                        }
                    )
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // You can pass a dummy NavController using rememberNavController for preview
    LoginScreen(navController = rememberNavController())
}