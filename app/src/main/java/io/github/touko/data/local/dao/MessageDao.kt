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
            (senderId = :uid AND receiverId = :friendId) OR
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
        WITH RankedMessages AS (
            SELECT 
                CASE 
                    WHEN senderId = :uid THEN receiverId 
                    ELSE senderId 
                END AS friendId,
                content,
                createTime,
                ROW_NUMBER() OVER (
                    PARTITION BY CASE WHEN senderId = :uid THEN receiverId ELSE senderId END 
                    ORDER BY createTime DESC
                ) as rn
            FROM message
            WHERE senderId = :uid OR receiverId = :uid  
        )
        SELECT friendId, content, createTime as lastMessageTime
        FROM RankedMessages
        WHERE rn = 1
        """
    )
    fun observeLastMessages(
        uid: Int
    ): Flow<List<LastMessageEntity>>

}