package com.motgolla.domain.record.api.service

import com.motgolla.domain.record.data.request.MemoSummaryRequest
import com.motgolla.domain.record.data.response.BarcodeInfoResponse
import com.motgolla.domain.record.data.request.RecordRequest
import com.motgolla.domain.record.data.response.MemoSummaryResponse
import com.motgolla.domain.record.data.response.RecordResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RecordService {

    // API 경로 지정 및 응답 데이터 지정
    @GET("/receipt/{receiptId}")
    fun getMemberReceiptInfo(@Path("receiptId") receiptId: Int): Call<RecordResponse>

    @Multipart
    @POST("/api/record/register")
    fun submitRecord(
        @Part("departmentStore") departmentStore: RequestBody,
        @Part tagImg: MultipartBody.Part?,
        @Part productImgs: List<MultipartBody.Part>,
        @Part("brandName") brandName: RequestBody,
        @Part("productId") productId: RequestBody,
        @Part("productName") productName: RequestBody,
        @Part("productNumber") productNumber: RequestBody,
        @Part("productSize") productSize: RequestBody,
        @Part("noteSummary") noteSummary: RequestBody
    ): Call<RecordResponse>

    @GET("/api/barcodes/{barcode}/product")
    fun getProductByBarcode(@Path("barcode") barcode: String): Call<BarcodeInfoResponse>

    @POST("/api/record/memo-summary")
    fun summarizeMemo(@Body request: MemoSummaryRequest): Call<MemoSummaryResponse>

}
