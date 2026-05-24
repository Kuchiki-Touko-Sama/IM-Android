package io.github.touko.data.local.repository

import android.util.Log
import io.github.touko.App
import io.github.touko.data.local.entity.MessageEntity
import io.github.touko.data.local.entity.toMessage
import io.github.touko.data.local.mapper.toEntity
import io.github.touko.data.model.response.LastMessage
import io.github.touko.data.model.response.Message
import io.github.touko.data.remote.HttpClient
import io.github.touko.data.remote.safeApiCall
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
    suspend fun fetchHistoryFromServer(uid: Int) {
        safeApiCall { HttpClient.messageApi.history(uid) }
            .onSuccess { response ->
                if (response.isSuccess() && response.data != null) {
                    for (message in response.data)
                        messageDao.insert(message.toEntity())
                }
            }
            .onFailure { Log.d("Touko", "fetchHistoryFromServer: ${it.message}") }
    }

    suspend fun syncMessagesFromServer(uid: Int) {
        val lastMessageId = messageDao.getLastMessageId()
        safeApiCall { HttpClient.messageApi.sync(uid, lastMessageId) }
            .onSuccess { response ->
                if (response.isSuccess() && response.data != null) {
                    for (message in response.data)
                        messageDao.insert(message.toEntity())
                }
            }
            .onFailure { Log.d("Touko", "syncMessagesFromServer: ${it.message}") }
    }

    suspend fun saveMessage(entity: MessageEntity) {
        try {
            messageDao.insert(entity)
        } catch (e: Exception) {
            Log.d("im-server", "saveMessage: ${e.message}")
        }
    }
}