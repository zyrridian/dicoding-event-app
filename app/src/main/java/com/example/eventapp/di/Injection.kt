package com.example.eventapp.di

import android.content.Context
import com.example.eventapp.data.local.room.EventDatabase
import com.example.eventapp.data.remote.EventRepository
import com.example.eventapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}