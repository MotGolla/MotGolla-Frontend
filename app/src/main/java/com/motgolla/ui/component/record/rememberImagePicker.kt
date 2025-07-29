package com.motgolla.ui.component.record
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
/**
 * 카메라 전용 런처 + 다중 선택 런처 통합
 * multiple = false → 카메라만
 * multiple = true  → 시스템 갤러리 다중 선택
 */
@Composable
fun rememberImagePicker(
    onSingle: (Uri?) -> Unit,
    onMulti: (List<Uri>) -> Unit
): (multiple: Boolean) -> Unit {
    val context = LocalContext.current
    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    // 1) 카메라 결과 수신
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSingle(cameraUri.value)
        }
    }

    // 2) 다중 선택(시스템 갤러리)
    val multiLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        onMulti(uris)
    }

    return { multiple ->
        if (multiple) {
            // 상품/착샷: 갤러리 다중 선택
            multiLauncher.launch("image/*")
        } else {
            // 택 사진: 카메라만
            val photoFile = File(context.cacheDir, "tag_photo.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            cameraUri.value = uri

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            cameraLauncher.launch(cameraIntent)
        }
    }
}
