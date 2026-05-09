package io.github.touko.data.remote.api

import io.github.touko.data.model.response.GetFriendshipResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FriendApi {
    @POST("friend/getFriendship")
    suspend fun getFriendship(@Body userId: Int): GetFriendshipResponse
}