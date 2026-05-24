package io.github.touko.data.remote.api

import io.github.touko.data.model.response.GetMessageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MessageApi {
    @GET("message/sync")
    suspend fun sync(@Query("userId") userId: Int, @Query("lastMessageId") lastMessageId: Int) : GetMessageResponse
    @GET("message/history")
    suspend fun history(@Query("userId") userId: Int): GetMessageResponse
}


