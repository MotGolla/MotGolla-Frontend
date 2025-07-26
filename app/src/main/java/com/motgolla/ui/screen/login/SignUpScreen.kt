package com.motgolla.ui.screen.login

import android.util.Log
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.navigation.NavController
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.login.api.service.SignUpService
import com.motgolla.domain.login.data.SignUpRequest
import com.motgolla.domain.login.data.TokenResponse
import kotlinx.coroutines.launch

fun Int.pad2(): String = toString().padStart(2, '0')

@Composable
fun SignUpScreen(
    navController: NavController,
    idToken: String,
    oauthId: String,
    nickname: String
) {
    var gender by remember { mutableStateOf("F") }
    val context = LocalContext.current
    var selectedYear by remember { mutableStateOf(2000) }
    var selectedMonth by remember { mutableStateOf(1) }
    var selectedDay by remember { mutableStateOf(1) }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(40.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "당신에 대해 \n" +
                            "더 알고 싶어요! \uD83D\uDE0A",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 30.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("성별", color = Color.DarkGray, style = MaterialTheme.typography.bodyLarge)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = gender == "F",
                                onClick = { gender = "F" }
                            )
                            Text("여자", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = gender == "M",
                                onClick = { gender = "M" }
                            )
                            Text("남자", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Text("생년월일", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AndroidView(
                            factory = { context ->
                                NumberPicker(context).apply {
                                    minValue = 1980
                                    maxValue = 2024
                                    value = selectedYear
                                    setOnValueChangedListener { _, _, newVal ->
                                        selectedYear = newVal
                                    }
                                }
                            },
                            update = { it.value = selectedYear },
                            modifier = Modifier.weight(1f)
                        )

                        AndroidView(
                            factory = { context ->
                                NumberPicker(context).apply {
                                    minValue = 1
                                    maxValue = 12
                                    value = selectedMonth
                                    setOnValueChangedListener { _, _, newVal ->
                                        selectedMonth = newVal
                                    }
                                }
                            },
                            update = { it.value = selectedMonth },
                            modifier = Modifier.weight(1f)
                        )

                        AndroidView(
                            factory = { context ->
                                NumberPicker(context).apply {
                                    minValue = 1
                                    maxValue = 31
                                    value = selectedDay
                                    setOnValueChangedListener { _, _, newVal ->
                                        selectedDay = newVal
                                    }
                                }
                            },
                            update = { it.value = selectedDay },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    // 생년월일 포맷: yyyy-MM-dd
                    val birthday = "${selectedYear}-${selectedMonth.pad2()}-${selectedDay.pad2()}"
                    // 서버로 회원 가입 요청
                    val signUpRequest = SignUpRequest(
                        idToken = idToken,
                        oauthId = oauthId,
                        name = nickname,
                        gender = when (gender) {
                            "MALE" -> "남자"
                            "FEMALE" -> "여자"
                            else -> gender
                        },
                        birthday = birthday
                    )
                    Log.d("SignUp", "회원 가입 요청: $signUpRequest")
                    SignUpService.requestSignUp(
                        signUpRequest = signUpRequest,
                        onSuccess = { accessToken, refreshToken ->
                            Log.d("SignUp", "회원 가입 성공: $accessToken, $refreshToken")
                            TokenStorage.save(context, TokenResponse(accessToken, refreshToken))
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                navController.navigate("welcome") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            }
                        },
                        onFailure = { throwable ->
                            Log.e("SignUp", "회원 가입 실패: ${throwable.message}")
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                android.widget.Toast.makeText(context, "회원가입 실패: ${throwable.message}", android.widget.Toast.LENGTH_SHORT).show()
                                navController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBFA2FF))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("시작 하기", color = Color.White, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SignUpScreenPreview() {
    SignUpScreenPreviewOnly()
}

@Composable
fun SignUpScreenPreviewOnly() {
    // Preview용으로 NavController 없이 호출
    val dummyNavController = rememberNavController()
    SignUpScreen(
        navController = dummyNavController,
        idToken = "dummyIdToken",
        oauthId = "dummyOauthId",
        nickname = "dummyNickname"
    )
}