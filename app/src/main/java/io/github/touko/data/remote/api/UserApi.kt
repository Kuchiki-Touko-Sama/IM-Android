package io.github.touko.data.remote.api

import io.github.touko.data.model.request.LoginRequest
import io.github.touko.data.model.request.RegisterRequest
import io.github.touko.data.model.response.GetUsersResponse
import io.github.touko.data.model.response.LoginResponse
import io.github.touko.data.model.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface UserApi {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("user/signUp")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("user")
    suspend fun getUsersByName(@Query("username") username: String): GetUsersResponse

}