package io.github.touko.data.remote.api

import io.github.touko.data.model.request.SendFriendApplyRequest
import io.github.touko.data.model.response.BaseResponse
import io.github.touko.data.model.response.GetFriendshipApplyResponse
import io.github.touko.data.model.response.GetFriendshipResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendApi {
    @POST("friend/getFriendship")
    suspend fun getFriendship(@Body userId: Int): GetFriendshipResponse

    @POST("friendApply/sendFriendApply")
    suspend fun applyFriendship(@Body sendFriendApplyRequest: SendFriendApplyRequest)
    : BaseResponse<Boolean>

    @POST("friendApply/getFriendApply")
    suspend fun getFriendshipApply(@Body userId: Int): GetFriendshipApplyResponse

    @POST("friendApply/acceptFriendApply/{friendApplyId}")
    suspend fun acceptApply(@Path("friendApplyId") friendApplyId: Int): BaseResponse<Boolean>
}