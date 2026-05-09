package io.github.touko.data.model.response

import kotlinx.serialization.Serializable


typealias GetFriendshipResponse = BaseResponse<List<Friendship>>

@Serializable
data class Friendship (
    val friendshipId: Int,
    val friendId: Int,
    val friendName: String
)