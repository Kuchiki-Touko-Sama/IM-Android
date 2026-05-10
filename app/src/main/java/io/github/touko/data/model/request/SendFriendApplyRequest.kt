package io.github.touko.data.model.request

import kotlinx.serialization.Serializable


@Serializable
data class SendFriendApplyRequest (val userId: Int, val friendId: Int)