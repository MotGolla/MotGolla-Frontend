package com.motgolla.domain.record.api.repository

import com.motgolla.common.RetrofitClient
import com.motgolla.domain.record.api.service.RecordService
import com.motgolla.domain.record.data.request.MemoSummaryRequest
import com.motgolla.domain.record.data.response.BarcodeInfoResponse
import com.motgolla.domain.record.data.request.RecordRequest
import com.motgolla.domain.record.data.response.MemoSummaryResponse
import com.motgolla.domain.record.data.response.RecordResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 RecordService 구현체 전달
    private val recordService: RecordService by lazy {
        RetrofitClient.instance.create(RecordService::class.java)
    }

    fun getMemberReceiptInfo(receiptId: Int, onResult: (Result<RecordResponse>) -> Unit) {
        val call = recordService.getMemberReceiptInfo(receiptId)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<RecordResponse> {

            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<RecordResponse>, response: Response<RecordResponse>) {
                if (response.isSuccessful) {
                    onResult(Result.success(response.body()!!))
                } else {
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<RecordResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
    fun submitRecord(
        departmentStore: RequestBody,
        tagImg: MultipartBody.Part?,
        productImgs: List<MultipartBody.Part>,
        brandName: RequestBody,
        productId: RequestBody,
        productName: RequestBody,
        productNumber: RequestBody,
        productSize: RequestBody,
        noteSummary: RequestBody,
        onResult: (Result<RecordResponse>) -> Unit
    ) {
        recordService.submitRecord(
            departmentStore,
            tagImg,
            productImgs,
            brandName,
            productId,
            productName,
            productNumber,
            productSize,
            noteSummary
        ).enqueue(object : Callback<RecordResponse> {
            override fun onResponse(call: Call<RecordResponse>, response: Response<RecordResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    onResult(Result.success(body))
                } else {
                    onResult(Result.failure(Throwable("서버 오류: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<RecordResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    // 바코드 스캔 정보 API
    fun getProductByBarcode(barcode: String, onResult: (Result<BarcodeInfoResponse>) -> Unit) {
        recordService.getProductByBarcode(barcode)
            .enqueue(object : Callback<BarcodeInfoResponse> {
                override fun onResponse(call: Call<BarcodeInfoResponse>, response: Response<BarcodeInfoResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        onResult(Result.success(response.body()!!))
                    } else {
                        val errorMessage = try {
                            val errorBody = response.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                                json["message"]?.asString ?: "알 수 없는 오류"
                            } else {
                                "서버 오류: 응답 본문 없음"
                            }
                        } catch (e: Exception) {
                            "에러 메시지 파싱 실패"
                        }

                        onResult(Result.failure(Throwable(errorMessage)))
                    }
                }

                override fun onFailure(call: Call<BarcodeInfoResponse>, t: Throwable) {
                    onResult(Result.failure(t))
                }
            })
    }

    // 메모 정리/요약 가져오기
    fun summarizeMemo(sttMemo: String, onResult: (Result<String>) -> Unit) {
        val request = MemoSummaryRequest(sttMemo)

        recordService.summarizeMemo(request).enqueue(object : Callback<MemoSummaryResponse> {
            override fun onResponse(
                call: Call<MemoSummaryResponse>,
                response: Response<MemoSummaryResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    onResult(Result.success(body.summary))
                } else {
                    onResult(Result.failure(Throwable("서버 오류: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<MemoSummaryResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }




}