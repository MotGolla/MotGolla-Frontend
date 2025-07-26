package com.motgolla.domain.departmentstore.api.service

import retrofit2.Response
import com.motgolla.domain.departmentstore.data.DepartmentStoreResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DepartmentStoreService {
    @GET("/api/department-store/nearest")
    suspend fun getNearestStore(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<DepartmentStoreResponse>
}