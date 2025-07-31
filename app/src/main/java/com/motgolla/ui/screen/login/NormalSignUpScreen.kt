package com.motgolla.ui.screen.login

import androidx.navigation.compose.rememberNavController
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.navigation.NavController
import com.motgolla.domain.login.api.service.SignUpService
import com.motgolla.ui.theme.MotgollaPrimary
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.login.data.NormalSignUpRequest
import com.motgolla.common.storage.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

import com.motgolla.R

@Composable
fun NormalSignUpScreen(
    navController: NavController
) {
    var gender by remember { mutableStateOf("F") }
    val context = LocalContext.current
    var selectedYear by remember { mutableStateOf(2000) }
    var selectedMonth by remember { mutableStateOf(1) }
    var selectedDay by remember { mutableStateOf(1) }

    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

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
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 30.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("아이디") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = if (Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}\$").matches(it)) {
                                ""
                            } else {
                                "영문+숫자 조합 8~20자여야 합니다"
                            }
                        },
                        label = { Text("비밀번호") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
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
                    performNormalSignUp(
                        context = context,
                        userId = userId,
                        password = password,
                        gender = gender,
                        year = selectedYear,
                        month = selectedMonth,
                        day = selectedDay,
                        navController = navController
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MotgollaPrimary),
                enabled = userId.isNotBlank() && passwordError.isEmpty() && password.isNotBlank()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("시작하기", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun NormalSignUpScreenPreview() {
    NormalSignUpScreenPreviewOnly()
}

@Composable
fun NormalSignUpScreenPreviewOnly() {
    // Preview용으로 NavController 없이 호출
    val dummyNavController = rememberNavController()
    SignUpScreen(
        navController = dummyNavController,
    )
}

private fun performNormalSignUp(
    context: android.content.Context,
    userId: String,
    password: String,
    gender: String,
    year: Int,
    month: Int,
    day: Int,
    navController: NavController
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val birthday = "%04d-%02d-%02d".format(year, month, day)
            val request = NormalSignUpRequest(
                oauthId = userId,
                password = password,
                name = userId,
                gender = gender,
                birthday = birthday,
                profile = context.resources.getResourceEntryName(R.drawable.heendy)
            )
            val response = RetrofitClient.getAuthService().signUp(request)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let { tokenResponse ->
                        TokenStorage.save(context, tokenResponse)
                        navController.navigate("welcome") {
                            popUpTo("normalSignup") { inclusive = true }
                        }
                    }
                } else if (response.code() == 400) {
                    // 서버에서 에러 body를 파싱해서 code 확인
                    val errorBody = response.errorBody()?.string()
                    if (errorBody?.contains("MEMBER002") == true) {
                        Toast.makeText(context, "이미 존재하는 사용자입니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "회원가입 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}