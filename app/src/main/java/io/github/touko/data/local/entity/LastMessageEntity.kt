package io.github.touko.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "last_message_cache")
data class LastMessageEntity(
    @PrimaryKey
    val friendId: Int,
    val content: String,
    val lastMessageTime: String
)
