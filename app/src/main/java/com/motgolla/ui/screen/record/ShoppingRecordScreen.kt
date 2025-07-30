package com.motgolla.ui.screen.record

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.motgolla.R
import com.motgolla.ui.component.record.FloorSelector
import com.motgolla.ui.component.record.MemoItem
import com.motgolla.ui.component.record.RecordImageItem
import com.motgolla.ui.component.record.rememberImagePicker
import com.motgolla.util.PreferenceUtil
import com.motgolla.viewmodel.record.MemoViewModel
import com.motgolla.viewmodel.record.RecordRegisterViewModel
import java.io.File

@Composable
fun ShoppingRecordScreen(
    viewModel: RecordRegisterViewModel,
    memoViewModel: MemoViewModel, navController: NavController
) {
    val context = LocalContext.current

    val inPreview = LocalInspectionMode.current

    //예시화면

    // 컴포저블이 처음 실행될 때 SharedPreference에서 읽어오기
    LaunchedEffect(Unit) {
        val savedId = PreferenceUtil.getDepartmentId(context)
        if (savedId != null) {
            viewModel.setDepartmentStoreId(savedId)
        } else {
            // ID가 없을 경우 예외 처리 또는 기본 동작 설정
            Log.w("DepartmentInit", "저장된 백화점 ID가 없습니다.")
        }

    }

    // 카메라 권한
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }
    LaunchedEffect(Unit) {
        if (!hasCameraPermission && !inPreview) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    if (!hasCameraPermission && !inPreview) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "카메라 권한이 필요합니다.\n앱 설정에서 권한을 허용해 주세요.",
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // 이미지 피커
    val pickImage = rememberImagePicker(
        onSingle = { uri ->
            uri?.let { viewModel.updateUiTagImageFile(uriToFile(context, it)) }
        },
        onMulti = { uris ->
            viewModel.addClothingImageFiles(uris.map { uriToFile(context, it) })
        }
    )
    // 상태
    val uiTagImageFile = viewModel.uiTagImageFile
    val apiTagImageFile by viewModel.apiTagImageFile.collectAsState()
    val productImageFiles by viewModel.productImageFiles.collectAsState()
    val departmentStoreId by viewModel.departmentStoreId.collectAsState()
    val brand by viewModel.brand.collectAsState()
    val model by viewModel.model.collectAsState()
    val modelNumber by viewModel.modelNumber.collectAsState()
    val modelSize by viewModel.modelSize.collectAsState()
    val memo by viewModel.memo.collectAsState()
    val barcodeInfo by viewModel.barcodeInfo.collectAsState()
    val maxPhotos = 3

    val barcodeLoading by viewModel.barcodeLoading.collectAsState()
    val barcodeSuccessMessage by viewModel.barcodeSuccessMessage.collectAsState()
    val barcodeErrorMessage by viewModel.barcodeErrorMessage.collectAsState()



    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 택 사진
        item {
            Text("택 사진", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .border(1.dp, Color(0xFFE6E8EB), RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .then(
                            if (!barcodeLoading) {
                                Modifier.pointerInput(Unit) {
                                    detectTapGestures { pickImage(false) }
                                }
                            } else Modifier // 로딩 중엔 클릭 막음
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiTagImageFile == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("택 사진", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Image(
                                painter = painterResource(R.drawable.record_plus_circle),
                                contentDescription = "추가 아이콘",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(uiTagImageFile),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        uiTagImageFile == null -> {
                            // ① 사진 찍기 전
                            Text(
                                "택에 바코드가 보이도록 사진을 찍어주세요.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        barcodeLoading -> {
                            // ② 바코드 인식 중
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .offset(x = 70.dp, y = 8.dp), // x만큼 오른쪽, y는 위쪽 여백
                                strokeWidth = 2.dp,
                                color = Color.Gray
                            )
                        }

                        barcodeErrorMessage.isNotEmpty() -> {
                            // ③ 서버 연결 실패 또는 응답 에러
                            Text(
                                barcodeErrorMessage,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        barcodeSuccessMessage.isNotEmpty() -> {
                            // ④ 성공
                            Text(
                                barcodeSuccessMessage,
                                color = Color(0xFF1D8348), // 초록색
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        else -> {
                            // ⑤ 바코드 인식 실패 (에러/성공 메시지도 없고 로딩도 아님)
                            Text(
                                "바코드가 인식되지 않았습니다. 다시 찍어주세요.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                }
            }


        }
        // 상품/착용샷 사진
        item {
            Text("상품/착용샷 사진", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(10.dp))

            if (productImageFiles.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, Color(0xFFE6E8EB), RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .pointerInput(Unit) { detectTapGestures { pickImage(true) } },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("상품/착용샷", style = MaterialTheme.typography.labelMedium)
                        Image(
                            painter = painterResource(R.drawable.record_plus_circle),
                            contentDescription = "추가 아이콘",
                            modifier = Modifier.size(24.dp)
                        )
                        Text("0/$maxPhotos", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            } else {
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    state = rememberLazyListState()
                ) {
                    items(productImageFiles) { file ->
                        RecordImageItem(
                            file = file,
                            onDelete = { viewModel.removeClothingImageFile(it) })
                    }
                    item {
                        val canAdd = productImageFiles.size < maxPhotos
                        Box(
                            Modifier
                                .size(100.dp)
                                .border(1.dp, Color(0xFFE6E8EB), RoundedCornerShape(8.dp))
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .pointerInput(canAdd) {
                                    detectTapGestures {
                                        if (canAdd) pickImage(
                                            true
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("상품/착용샷", style = MaterialTheme.typography.labelMedium)
                                Image(
                                    painter = painterResource(R.drawable.record_plus_circle),
                                    contentDescription = "추가 아이콘",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "${productImageFiles.size}/$maxPhotos",
                                    fontSize = 10.sp, color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            if (departmentStoreId != 0L) {
                FloorSelector(
                    departmentStoreId = departmentStoreId,
                    brandName = brand,
                    refreshTrigger = uiTagImageFile,
                    onLocationSelected = { selectedLocation ->
                        viewModel.setDepartmentStoreBrandId(selectedLocation)
                    }
                )
            }
        }
        // 브랜드명
        item {
            OutlinedTextField(
                value = brand,
                onValueChange = viewModel::setBrand,
                label = { Text("브랜드명") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE6E8EB))
            )
        }
        // 모델명
        item {
            OutlinedTextField(
                value = model,
                onValueChange = viewModel::setModel,
                label = { Text("모델명") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE6E8EB))
            )
        }
        // 모델번호
        item {
            OutlinedTextField(
                value = modelNumber,
                onValueChange = viewModel::setModelNumber,
                label = { Text("모델 번호") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE6E8EB))
            )
        }

        // 사이즈
        item {
            OutlinedTextField(
                value = modelSize,
                onValueChange = viewModel::setModelSize,
                label = { Text("사이즈") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE6E8EB))
            )
        }

        // 모델번호 뒤에 MemoItem 호출
        item { MemoItem(memoViewModel) }

        // 기록하기 버튼
        item {
            val isFormValid =
                brand.isNotBlank() && model.isNotBlank() && modelNumber.isNotBlank() && apiTagImageFile != null
            Button(
                onClick = {
                    viewModel.setMemo(memoViewModel.memo.value)
                    viewModel.submitRecord { /* 내일 추천 모달 추가 */
                        navController.navigate("home") {
                            popUpTo("shoppingRecord") { inclusive = true } // 필요 시 백스택 정리
                        }
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B2CBF),
                    contentColor = Color.White
                ),
                modifier = Modifier

                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("기록하기", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}


fun uriToFile(context: Context, uri: Uri): File {
    val input = context.contentResolver.openInputStream(uri)
    val tmp = File.createTempFile("tmp_", ".jpg", context.cacheDir)
    input?.use { it.copyTo(tmp.outputStream()) }
    return tmp
}

@Preview(showBackground = true)
@Composable
fun ShoppingRecordScreenPreview() {
    val fakeViewModel = RecordRegisterViewModel()
    val fakeMemoViewModel =
        MemoViewModel(Application())
    ShoppingRecordScreen(viewModel = fakeViewModel, memoViewModel = fakeMemoViewModel,  navController = rememberNavController())
}
