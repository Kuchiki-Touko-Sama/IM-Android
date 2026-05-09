package io.github.touko.data.remote.api

import io.github.touko.data.model.response.GetFriendshipResponse
import retrofit2.http.POST

interface FriendApi {
    @POST("getFriendship")
    suspend fun getFriendship(userId: Int): GetFriendshipResponse
}