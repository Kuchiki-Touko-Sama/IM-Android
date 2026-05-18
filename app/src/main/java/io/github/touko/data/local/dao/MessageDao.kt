package io.github.touko.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import io.github.touko.data.local.entity.LastMessageEntity
import io.github.touko.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MessageDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(message: MessageEntity)

    @Insert
    suspend fun insertAll(vararg messages: MessageEntity)

    @Delete
    suspend fun delete(message: MessageEntity)

    @Delete
    suspend fun deleteAll(vararg message: MessageEntity)

    @Query(
        """
        SELECT *
        FROM message
        WHERE
        (senderId = :uid AND receiverId = :friendId)
        OR
        (senderId = :friendId AND receiverId = :uid)
        ORDER BY messageId ASC
    """
    )
    fun observeMessageByFriendId(
        uid: Int,
        friendId: Int
    ): Flow<List<MessageEntity>>

    @Query(
        """
        SELECT
            CASE
                WHEN senderId = :uid THEN receiverId
                ELSE senderId
            END as friendId,
            content,
            MAX(createTime) as lastMessageTime
        FROM message
        WHERE (senderId = :uid AND receiverId IN (:friendIds))
           OR (senderId IN (:friendIds) AND receiverId = :uid)
        GROUP BY
            CASE
                WHEN senderId = :uid THEN receiverId
                ELSE senderId
            END
    """
    )
    fun getLastMessagesForFriends(
        uid: Int,
        friendIds: List<Int>
    ): Flow<List<LastMessageEntity>>

}