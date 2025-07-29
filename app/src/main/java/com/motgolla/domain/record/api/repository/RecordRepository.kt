package com.motgolla.domain.record.api.repository

import com.motgolla.common.RetrofitClient
import com.motgolla.domain.record.api.service.RecordService
import com.motgolla.domain.record.data.request.MemoSummaryRequest
import com.motgolla.domain.record.data.request.UpdateRecordStatusRequest
import com.motgolla.domain.record.data.response.BarcodeInfoResponse
import com.motgolla.domain.record.data.response.MemoSummaryResponse
import com.motgolla.domain.record.data.response.RecordDatesResponse
import com.motgolla.domain.record.data.response.RecordProductFilterListResponse
import com.motgolla.domain.record.data.response.RecordResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordRepository {

    private val recordService: RecordService by lazy {
        RetrofitClient.getRecordService()
    }

    fun getMemberReceiptInfo(receiptId: Int, onResult: (Result<RecordResponse>) -> Unit) {
        val call = recordService.getMemberReceiptInfo(receiptId)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<RecordResponse> {

            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(
                call: Call<RecordResponse>,
                response: Response<RecordResponse>
            ) {
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
        departmentStoreBrandId: RequestBody,
        tagImg: MultipartBody.Part?,
        productImgs: List<MultipartBody.Part>,
        productId: RequestBody,
        productSize: RequestBody,
        noteSummary: RequestBody,
        onResult: (Result<RecordResponse>) -> Unit
    ) {
        recordService.submitRecord(
            departmentStoreBrandId,
            tagImg,
            productImgs,
            productId,
            productSize,
            noteSummary
        ).enqueue(object : Callback<RecordResponse> {
            override fun onResponse(
                call: Call<RecordResponse>,
                response: Response<RecordResponse>
            ) {
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
    fun getProductByBarcode(
        barcode: String,
        departmentStoreId: Long,
        onResult: (Result<BarcodeInfoResponse>) -> Unit
    ) {
        recordService.getProductByBarcode(barcode, departmentStoreId)
            .enqueue(object : Callback<BarcodeInfoResponse> {
                override fun onResponse(
                    call: Call<BarcodeInfoResponse>,
                    response: Response<BarcodeInfoResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        onResult(Result.success(response.body()!!))
                    } else {
                        val errorMessage = try {
                            val errorBody = response.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                val json =
                                    com.google.gson.JsonParser.parseString(errorBody).asJsonObject
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

    // 쇼핑 기록 상세 조회 (무한 스크롤)
    fun getShoppingRecords(
        date: String,
        category: String?,
        cursor: Long?,
        limit: Int,
        onResult: (Result<RecordProductFilterListResponse>) -> Unit
    ) {
        recordService.getProducts(date, category, cursor, limit)
            .enqueue(object : Callback<RecordProductFilterListResponse> {
                override fun onResponse(
                    call: Call<RecordProductFilterListResponse>,
                    response: Response<RecordProductFilterListResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        onResult(Result.success(body))
                    } else {
                        onResult(Result.failure(Throwable("서버 오류: ${response.message()}")))
                    }
                }

                override fun onFailure(call: Call<RecordProductFilterListResponse>, t: Throwable) {
                    onResult(Result.failure(t))
                }
            })
    }

    // 상태 업데이트
    fun updateRecordStateToCompleted(recordId: Long, callback: (Result<Unit>) -> Unit) {
        val requestBody = UpdateRecordStatusRequest() // "COMPLETED" 기본값 사용
        recordService.updateRecordState(recordId, requestBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        callback(Result.failure(Exception("서버 오류: ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }


    // 월별 요일 가져오기
    fun getRecordDates(
        yearMonth: String,
        callback: (Result<RecordDatesResponse>) -> Unit
    ) {
        recordService.getRecordDates(yearMonth).enqueue(object : Callback<RecordDatesResponse> {
            override fun onResponse(
                call: Call<RecordDatesResponse>,
                response: Response<RecordDatesResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                } else {
                    callback(Result.failure(Exception("서버 오류: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<RecordDatesResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}