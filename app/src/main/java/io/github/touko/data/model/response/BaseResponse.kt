package io.github.touko.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)