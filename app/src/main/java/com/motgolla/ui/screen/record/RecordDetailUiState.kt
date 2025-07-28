package com.motgolla.ui.screen.record

import com.motgolla.domain.record.data.response.RecordDetailResponse

data class RecordDetailUiState(
    val record: RecordDetailResponse? = null,
    val errorMessage: String? = null
)