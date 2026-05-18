package io.github.touko.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import io.github.touko.data.model.response.Message

@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo
    val messageId: Int,

    @ColumnInfo
    val senderId: Int,
    @ColumnInfo
    val receiverId: Int,
    @ColumnInfo
    val content: String,
    @ColumnInfo
    val createTime: String
)

fun MessageEntity.toMessage(): Message {
    return Message(
        senderId = this.senderId,
        receiverId = this.receiverId,
        content = this.content,
        messageId = this.messageId,
        createTime = this.createTime
    )
}

fun MessageEntity.toLastMessageEntity(friendId: Int): LastMessageEntity {
    return LastMessageEntity(
        friendId = friendId,
        content = content,
        lastMessageTime = createTime
    )
}