package com.motgolla.domain.departmentstore.data

data class DepartmentStoreResponse(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val distance: Double
)