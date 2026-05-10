package io.github.touko.data.model.response

import kotlinx.serialization.Serializable


typealias GetUsersResponse = BaseResponse<List<TargetUser>>

@Serializable
data class TargetUser (val username: String, val userId: Int)