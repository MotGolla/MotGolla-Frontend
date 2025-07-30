package com.motgolla.viewmodel.record

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.motgolla.domain.record.api.repository.RecordRepository
import com.motgolla.domain.record.data.response.BarcodeInfoResponse
import com.motgolla.domain.record.data.response.RecordResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RecordRegisterViewModel : ViewModel() {

    private val recordRepository = RecordRepository()

    // 이미지 파일 상태 관리
    // UI용 이미지
    var uiTagImageFile by mutableStateOf<File?>(null)

    // API 전송용 이미지
    private val _apiTagImageFile = MutableStateFlow<File?>(null)
    val apiTagImageFile: StateFlow<File?> = _apiTagImageFile

    private val _productImageFiles = MutableStateFlow<List<File>>(emptyList())
    val productImageFiles: StateFlow<List<File>> = _productImageFiles

    private val _brand = MutableStateFlow("")
    val brand: StateFlow<String> = _brand


    private val _model = MutableStateFlow("")
    val model: StateFlow<String> = _model

    private val _modelNumber = MutableStateFlow("")
    val modelNumber: StateFlow<String> = _modelNumber


    // 현재 백화점명으로 바꿔야함 지금은 임시데이터
    private val _departmentStoreId = MutableStateFlow(0L)
    val departmentStoreId: StateFlow<Long> = _departmentStoreId


    private val _departmentStoreBrandId = MutableStateFlow(0L)
    val departmentStoreBrandId: StateFlow<Long> = _departmentStoreBrandId

    private val _modelSize = MutableStateFlow("")
    val modelSize: StateFlow<String> = _modelSize

    private val _productId = MutableStateFlow<Long>(0L)
    val productId: StateFlow<Long> = _productId

    private val _memo = MutableStateFlow("")
    val memo: StateFlow<String> = _memo

    // 바코드 정보 및 상태
    private val _scannedBarcode = MutableStateFlow("")
    val scannedBarcode: StateFlow<String> = _scannedBarcode

    // 바코드 정보
    private val _barcodeInfo = MutableStateFlow<BarcodeInfoResponse?>(null)
    val barcodeInfo: StateFlow<BarcodeInfoResponse?> = _barcodeInfo

    private val _barcodeSuccessMessage = MutableStateFlow("")
    val barcodeSuccessMessage: StateFlow<String> = _barcodeSuccessMessage

    private val _barcodeErrorMessage = MutableStateFlow("")
    val barcodeErrorMessage: StateFlow<String> = _barcodeErrorMessage

    private val _barcodeLoading = MutableStateFlow(false)
    val barcodeLoading: StateFlow<Boolean> = _barcodeLoading

    init {
        // 바코드 정보가 업데이트 되면 관련 필드들도 자동 업데이트
        viewModelScope.launch {
            barcodeInfo.collect { info ->
                info?.let {
                    setBrand(it.brand)
                    setModel(it.productName)
                    setModelNumber(it.productNumber)
                    setProductId(it.productId)
                }
            }
        }
    }

    // setter 함수들
    fun setApiTagImageFile(file: File?) {
        _apiTagImageFile.value = file
    }

    fun updateUiTagImageFile(file: File?) {
        uiTagImageFile = file
        file?.let {
            // UI에 표시한 후, 바코드 인식 시도
            scanBarcodeFromFile(file)
        }
    }

    fun addClothingImageFiles(files: List<File>) {
        _productImageFiles.value = (_productImageFiles.value + files).distinct().take(3)
    }

    fun removeClothingImageFile(file: File) {
        _productImageFiles.value = _productImageFiles.value - file
    }

    fun setBrand(value: String) {
        _brand.value = value
    }

    fun setModel(value: String) {
        _model.value = value
    }

    fun setModelNumber(value: String) {
        _modelNumber.value = value
    }

    fun setModelSize(value: String) {
        _modelSize.value = value
    }

    fun setProductId(value: Long) {
        _productId.value = value
    }

    fun setMemo(value: String) {
        _memo.value = value
    }

    fun setDepartmentStoreId(value: Long) {
        _departmentStoreId.value = value
    }

    fun setDepartmentStoreBrandId(value: Long) {
        _departmentStoreBrandId.value = value

    }


    fun applyBarcodeInfoToForm(info: BarcodeInfoResponse) {
        setBrand(info.brand ?: "")
        setModel(info.productName ?: "")
        setModelNumber(info.productNumber ?: "")
        setModelSize("") // 사이즈는 직접 입력 받는 경우 그대로 유지해도 됩니다
    }

    fun clearFormFields() {
        setBrand("")
        setModel("")
        setModelNumber("")
        setModelSize("")
    }

    // 바코드 스캔 및 서버 정보 요청
    fun scanBarcodeFromFile(imageFile: File) {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        _barcodeLoading.value = true
        _barcodeSuccessMessage.value = ""
        _barcodeErrorMessage.value = ""

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val rawValue = barcodes.firstOrNull()?.rawValue
                if (rawValue != null) {
                    _scannedBarcode.value = rawValue

                    recordRepository.getProductByBarcode(
                        rawValue,
                        _departmentStoreId.value
                    ) { result ->
                        result.onSuccess { response ->
                            _barcodeInfo.value = response
                            applyBarcodeInfoToForm(response) // 입력 필드 채우기
                            setApiTagImageFile(imageFile)
                            _barcodeSuccessMessage.value = "바코드 인식에 성공했어요!\n상품 정보를 불러왔습니다."
                        }.onFailure { e ->
                            _barcodeErrorMessage.value = e.message ?: "상품 정보를 불러올 수 없습니다."
                            setApiTagImageFile(null)
                            clearFormFields() // 초기화
                        }
                        _barcodeLoading.value = false
                    }
                } else {
                    _barcodeErrorMessage.value = "바코드를 인식하지 못했습니다."
                    setApiTagImageFile(null)
                    clearFormFields() // 초기화
                    _barcodeLoading.value = false
                }
            }
            .addOnFailureListener {
                _barcodeErrorMessage.value = it.message ?: "바코드 스캔에 실패했습니다."
                setApiTagImageFile(null)
                clearFormFields() // 초기화
                _barcodeLoading.value = false
            }
    }

    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
    }


    // File -> MultipartBody.Part 변환 함수
    private fun fileToPart(partName: String, file: File?): MultipartBody.Part? =
        file?.let {
            val mimeType =
                getMimeType(it).toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
            val requestFile = it.asRequestBody(mimeType)
            MultipartBody.Part.createFormData(partName, it.name, requestFile)
        }

    private fun filesToParts(partName: String, files: List<File>): List<MultipartBody.Part> =
        files.mapNotNull { file ->
            val mimeType =
                getMimeType(file).toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mimeType)
            MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }

    // 실제 서버에 개별 파트로 전송하는 함수
    fun submitRecord(onResult: (Result<RecordResponse>) -> Unit) {
        val tagImgPart = fileToPart("tagImg", apiTagImageFile.value)
        val productImgParts = filesToParts("productImgs", productImageFiles.value)

        val departmentStoreBrandIdPart: RequestBody = departmentStoreBrandId.value.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val productIdPart: RequestBody =
            productId.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val productSizePart: RequestBody =
            modelSize.value.toRequestBody("text/plain".toMediaTypeOrNull())
        val noteSummaryPart: RequestBody =
            memo.value.toRequestBody("text/plain".toMediaTypeOrNull())

        recordRepository.submitRecord(
            departmentStoreBrandIdPart,
            tagImgPart,
            productImgParts,
            productIdPart,
            productSizePart,
            noteSummaryPart,
            onResult
        )
    }
}
