package com.example.myapplication.domain.example.api.repository

import com.example.myapplication.common.RetrofitClient
import com.example.myapplication.domain.example.api.service.ExampleService
import com.example.myapplication.domain.example.data.ExampleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExampleRepository {

    // RetrofitClient 기반으로 인스턴스 생성 후 ExampleService 구현체 전달
    private val exampleService: ExampleService by lazy {
        RetrofitClient.instance.create(ExampleService::class.java)
    }

    fun getMemberReceiptInfo(receiptId: Int, onResult: (Result<ExampleResponse>) -> Unit) {
        val call = exampleService.getMemberReceiptInfo(receiptId)

        // 비동기적으로 API 요청 수행
        call.enqueue(object : Callback<ExampleResponse> {

            // 웅답이 성공적으로 호출되었다면
            override fun onResponse(call: Call<ExampleResponse>, response: Response<ExampleResponse>) {
                if (response.isSuccessful) {
                    onResult(Result.success(response.body()!!))
                } else {
                    onResult(Result.failure(Throwable(response.message())))
                }
            }

            // 응답 실패
            override fun onFailure(call: Call<ExampleResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}