package com.motgolla.ui.screen.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.login.data.NormalLoginRequest
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalLoginScreen(
    navController: NavController
){
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("로그인") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("아이디") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        performNormalLogin(
                            context,
                            userId,
                            password,
                            navController
                        ) { isLoading = it }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    enabled = userId.isNotBlank() && password.isNotBlank() && !isLoading
                ) {
                    Text(if (isLoading) "로그인 중..." else "로그인")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { navController.navigate("normalSignup") }
                ) {
                    Text("회원가입")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun performNormalLogin(
    context: Context,
    userId: String,
    password: String,
    navController: NavController,
    setLoading: (Boolean) -> Unit
) {
    setLoading(true)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val normalLoginRequest = NormalLoginRequest(
                id = userId,
                password = password
            )
            val response = RetrofitClient.getAuthService().login(normalLoginRequest)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let { tokenResponse ->
                        TokenStorage.save(context, tokenResponse)
                        navController.navigate("welcome") {
                            popUpTo("normalLogin") { inclusive = true }
                        }
                    } ?: Toast.makeText(context, "로그인 실패: 토큰 정보가 없습니다", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 401) {
                    Toast.makeText(context, "존재하지 않는 사용자입니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "로그인 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } finally {
            setLoading(false)
        }
    }
}