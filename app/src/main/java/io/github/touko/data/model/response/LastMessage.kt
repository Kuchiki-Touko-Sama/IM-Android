package io.github.touko.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LastMessage(
    val friendId: Int,
    val content: String,
    val lastMessageTime: String
)
