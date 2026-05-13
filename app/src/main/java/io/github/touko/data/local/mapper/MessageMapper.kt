package io.github.touko.data.local.mapper

import io.github.touko.data.local.entity.MessageEntity
import io.github.touko.data.model.response.Message

fun Message.toEntity(): MessageEntity {

    return MessageEntity(
        messageId = messageId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        createTime = createTime
    )
}