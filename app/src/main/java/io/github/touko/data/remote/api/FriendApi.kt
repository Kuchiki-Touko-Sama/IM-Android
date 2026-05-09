package io.github.touko.data.remote.api

import retrofit2.http.POST

interface FriendApi {
    @POST("getFriendship")
    suspend fun getFriendship(userId: Int):
}