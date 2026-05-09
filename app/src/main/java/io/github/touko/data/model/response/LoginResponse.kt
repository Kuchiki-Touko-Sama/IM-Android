package io.github.touko.data.model.response

import kotlinx.serialization.Serializable

typealias LoginResponse = BaseResponse<LoginData>

@Serializable
data class LoginData(
    val token: String,
    val userId: Int
)
