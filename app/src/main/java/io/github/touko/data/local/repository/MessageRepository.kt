package io.github.touko.data.local.repository

import android.util.Log
import io.github.touko.App
import io.github.touko.data.local.entity.MessageEntity
import io.github.touko.data.local.entity.toMessage
import io.github.touko.data.local.mapper.toEntity
import io.github.touko.data.model.response.LastMessage
import io.github.touko.data.model.response.Message
import io.github.touko.data.remote.HttpClient.messageApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepository {
    private val messageDao by lazy { App.db.messageDao() }
    fun observeMessages(uid: Int, friendId: Int): Flow<List<Message>> {
        return messageDao
            .observeMessageByFriendId(uid, friendId)
            .map { entities ->
                entities.map { it.toMessage() }
            }
    }

    fun observeLastMessages(uid: Int): Flow<Map<Int, LastMessage>> {
        return messageDao.observeLastMessages(uid)
            .map { entities ->
                entities.associate { entity ->
                    entity.friendId to LastMessage(
                        friendId = entity.friendId,
                        content = entity.content,
                        lastMessageTime = entity.lastMessageTime
                    )
                }
            }
    }
    suspend fun fetchHistoryFromServer(myId: Int) {
        try {
            val response = messageApi.history(myId)
            if (response.code == 200 && response.data != null) {
                //写入数据库，Flow 自动感应并刷新 UI
                for (message in response.data)
                    messageDao.insert(message.toEntity())
            }
        } catch (e: Exception) {
            Log.d("im-server", "fetchHistoryFromServer: ${e.message}")
        }
    }

    suspend fun saveMessage(entity: MessageEntity) {
        try {
            messageDao.insert(entity)
        } catch (e: Exception) {
            Log.d("im-server", "saveMessage: ${e.message}")
        }
    }
}