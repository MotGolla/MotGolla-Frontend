package com.motgolla.domain.record.api.service

import com.motgolla.domain.recommend.data.ProductPreview
import com.motgolla.domain.record.data.request.MemoSummaryRequest
import com.motgolla.domain.record.data.response.BrandLocationResponse
import com.motgolla.domain.record.data.request.UpdateRecordStatusRequest
import com.motgolla.domain.record.data.response.BarcodeInfoResponse
import com.motgolla.domain.record.data.response.MemoSummaryResponse
import com.motgolla.domain.record.data.response.RecordDatesResponse
import com.motgolla.domain.record.data.response.RecordProductFilterListResponse
import com.motgolla.domain.record.data.response.RecordDetailResponse
import com.motgolla.domain.record.data.response.RecordResponse
import com.motgolla.domain.record.data.response.ShoppingHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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


    @GET("/api/record/products")
    fun getProducts(
        @Query("date") date: String,
        @Query("category") category: String?,
        @Query("cursor") cursor: Long?,
        @Query("limit") limit: Int
    ): Call<RecordProductFilterListResponse>


    @PATCH("api/record/{recordId}/status")
    suspend fun updateRecordState(
        @Path("recordId") recordId: Long,
        @Body request: UpdateRecordStatusRequest
    ): retrofit2.Response<Void>


    @GET("/api/record/dates")
    fun getRecordDates(
        @Query("yearMonth") yearMonth: String
    ): Call<RecordDatesResponse>

    @GET("/api/record/{recordId}")
    suspend fun getRecordById(@Path("recordId") recordId: Long): RecordDetailResponse

    @GET("/api/product/{productId}/recommend")
    suspend fun getRecommendedProducts(@Path("productId") productId: Long): List<ProductPreview>

    @GET("/api/record/products")
    suspend fun getShoppingHistory(
        @Query("date") date: String,
        @Query("limit") limit: Int
    ): ShoppingHistoryResponse

    @GET("/api/department-store-brand/locations")
    suspend fun getDepartmentStoreLocations(
        @Query("departmentStoreId") departmentStoreId: Long,
        @Query("brandName") brandName: String
    ): List<BrandLocationResponse>
}
