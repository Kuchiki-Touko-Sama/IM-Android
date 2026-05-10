package io.github.touko.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val senderId: Int,
    val receiverId: Int,
    val content: String,
) {
}