package com.motgolla.domain.record.view.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
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
import coil.compose.rememberAsyncImagePainter
import com.motgolla.R
import java.io.File

@Composable
fun ShoppingRecordScreen(viewModel: RecordViewModel, memoViewModel: MemoViewModel) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

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
        onSingle = { uri -> uri?.let { viewModel.setTagImageFile(uriToFile(context, it)) } },
        onMulti = { uris -> viewModel.addClothingImageFiles(uris.map { uriToFile(context, it) }) }
    )

    // 상태
    val tagImageFile by viewModel.tagImageFile.collectAsState()
    val productImageFiles by viewModel.productImageFiles.collectAsState()
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
        // 상단바
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("상단바 (더미)", fontSize = 14.sp)
            }
        }

        // 택 사진
        item {
            Text("택 사진", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        .pointerInput(Unit) { detectTapGestures { pickImage(false) } },
                    contentAlignment = Alignment.Center
                ) {
                    if (tagImageFile == null) {
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
                            painter = rememberAsyncImagePainter(tagImageFile!!),
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
                        tagImageFile == null -> {
                            // ① 사진 찍기 전
                            Text(
                                "택에 바코드가 보이도록 사진을 찍어주세요.",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        barcodeLoading -> {

                        }
                        barcodeSuccessMessage.isNotEmpty() -> {
                            // ③ 성공
                            Text(
                                barcodeSuccessMessage,
                                color = Color(0xFF1D8348),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        barcodeErrorMessage.isNotEmpty() -> {
                            // ④ 서버 연결 실패 또는 응답 에러
                            Text(
                                barcodeErrorMessage,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        else -> {
                            // ⑤ 바코드 인식 실패
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

            // 택 이미지를 올릴 때마다 바코드 인식 트리거
            LaunchedEffect(tagImageFile) {
                tagImageFile?.let { file ->
                    viewModel.scanBarcodeFromFile(context, file)
                }
            }
        }
        // 상품/착용샷 사진
        item {
            Text("상품/착용샷 사진", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        Text("상품/착용샷", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Image(
                            painter = painterResource(R.drawable.record_plus_circle),
                            contentDescription = "추가 아이콘",
                            modifier = Modifier.size(24.dp)
                        )
                        Text("0/$maxPhotos", fontSize = 10.sp, color = Color.Gray)
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
                                Text("상품/착용샷", fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
            val isFormValid = brand.isNotBlank() && model.isNotBlank() && modelNumber.isNotBlank()
            Button(
                onClick = { viewModel.setMemo(memoViewModel.memo.value)
                    viewModel.submitRecord { /* 결과 처리 추후 nav로 이동 */ } },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B2CBF),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("기록하기")
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
    val fakeViewModel = RecordViewModel()
    val fakeMemoViewModel = MemoViewModel(Application())
    ShoppingRecordScreen(viewModel = fakeViewModel, memoViewModel = fakeMemoViewModel)
}
