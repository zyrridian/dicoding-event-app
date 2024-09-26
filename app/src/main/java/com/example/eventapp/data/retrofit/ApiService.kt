package com.example.eventapp.data.retrofit

import com.example.eventapp.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    fun getEvents(
        @Query("active") active: Int = 1, // 1 upcoming, 0 finished, -1 all
        @Query("q") query: String? = null,
        @Query("limit") limit: Int = 40,
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getEventById(
        @Path("id") id: Int,
    )

}