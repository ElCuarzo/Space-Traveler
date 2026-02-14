package com.example.spacetraveler.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

interface MissionApi {

    companion object {
        fun create(): MissionApi {
            return Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MissionApi::class.java)
        }
    }

    @GET("missions")
    suspend fun getMissions(): Response<List<MissionDto>>

    @GET("missions/{id}")
    suspend fun getMissionById(@Path("id") id: Int): Response<MissionDto>

    @POST("missions")
    suspend fun createMission(@Body mission: MissionDto): Response<MissionDto>

    @PUT("missions/{id}")
    suspend fun updateMission(@Path("id") id: Int, @Body mission: MissionDto): Response<MissionDto>

    @DELETE("missions/{id}")
    suspend fun deleteMission(@Path("id") id: Int): Response<Unit>
}
