package io.github.touko.data.model.response

import kotlinx.serialization.Serializable

typealias GetMessageResponse = BaseResponse<List<Message>>

@Serializable
data class Message(
    val messageId: Int,
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val createTime: String
)