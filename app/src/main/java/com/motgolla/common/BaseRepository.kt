package com.motgolla.common

import com.google.gson.Gson
import com.motgolla.common.data.ErrorResponse
import retrofit2.Response

open class BaseRepository {

    /**
     * 공통 응답 처리 함수
     */
    protected fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception(response.errorBody()?.string())
        }
    }

    /**
     * 공통 네트워크 예외 처리 함수
     */
    protected fun handleNetworkException(e: Exception): Exception {
        return try {
            // 에러 메시지가 JSON 형식일 경우 ErrorRes로 파싱
            val errorResponse = Gson().fromJson(e.message, ErrorResponse::class.java)
            Exception(errorResponse.message)
        } catch (jsonEx: Exception) {
            // JSON 파싱 실패 시, 일반 네트워크 에러로 처리
            Exception("Network error: ${e.localizedMessage}")
        }
    }
}