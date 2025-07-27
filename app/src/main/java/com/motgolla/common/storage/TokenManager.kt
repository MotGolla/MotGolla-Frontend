package com.motgolla.common.storage

import kotlinx.coroutines.flow.MutableSharedFlow

// TokenManager.kt
object TokenManager {
    val tokenErrorFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}