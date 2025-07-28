package com.motgolla.ui.screen.my

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.motgolla.common.RetrofitClient
import com.motgolla.common.storage.TokenStorage
import com.motgolla.domain.login.api.service.MemberService
import com.motgolla.ui.component.modal.RecommendModal

@Composable
fun MyScreen(navController: NavController) {
    val context = LocalContext.current

    // 상태로 값 저장
    var nickname by remember { mutableStateOf("닉네임") }
    var profile by remember { mutableStateOf("https://via.placeholder.com/60") }
    var birthday by remember { mutableStateOf("생일 정보 없음") }
    var gender by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("2025-06-02") }

    var checked by remember {
        mutableStateOf(
            when (TokenStorage.getValue(context, "notificationCheck")) {
                "true" -> true
                "false" -> false
                else -> false
            }
        )
    }

    // 코루틴으로 서버에서 값 불러오기
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.getAuthService().getMemberInfo()
            if (response.isSuccessful) {
                response.body()?.let { memberInfo ->
                    nickname = memberInfo.name
                    profile = memberInfo.profile?.takeIf { it.isNotBlank() } ?: "https://via.placeholder.com/60"
                    birthday = memberInfo.birthday
                    gender = memberInfo.gender
                    createdAt = memberInfo.createdAt.toString().take(10)
                }
            } else {
                println("회원 정보 조회 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResignDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF2FC8AB), Color(0xFF2CC0E1))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(profile),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = nickname, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text(text = "${createdAt} 가입", color = Color.White, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Birthday and Gender Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Cake, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = birthday, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))

            if(gender == "F")
                Icon(Icons.Default.Female, contentDescription = null)
            else if(gender == "M")
                Icon(Icons.Default.Male, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(35.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "로그아웃",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { showLogoutDialog = true }
            )

            Text(
                text = "회원탈퇴",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { showResignDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "알림 설정", fontSize = 17.sp)
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = checked, onCheckedChange = {
                checked = !checked
                onClickChecked(context, checked)
            })
        }
    }



    // Confirmation dialogs
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("정말 로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onClickLogout(context, navController)
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    if (showResignDialog) {
        AlertDialog(
            onDismissRequest = { showResignDialog = false },
            title = { Text("회원탈퇴") },
            text = { Text("정말 회원탈퇴 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showResignDialog = false
                    onClickResign(context, navController)
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResignDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

fun onClickChecked(context: Context, checked: Boolean){
    Log.d("MyScreen", "onClickChecked: $checked")
    TokenStorage.save(context, "notificationCheck", checked.toString())
}

fun onClickLogout(context: Context, navController: NavController){
    MemberService.logout(context, navController)
}

fun onClickResign(context: Context, navController: NavController){
    MemberService.resign(context, navController)
}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    MyScreen(navController = rememberNavController())
}