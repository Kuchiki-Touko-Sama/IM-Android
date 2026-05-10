package io.github.touko.data.model.response

import kotlinx.serialization.Serializable

typealias GetFriendshipApplyResponse = BaseResponse<List<FriendshipApply>>

@Serializable
data class FriendshipApply(
    val friendApplyId: Int,
    val senderId: Int,
    val userName: String
)