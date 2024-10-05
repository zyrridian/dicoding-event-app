package com.example.eventapp.di

import android.content.Context
import com.example.eventapp.data.local.room.EventDatabase
import com.example.eventapp.data.remote.EventRepository
import com.example.eventapp.data.remote.retrofit.ApiConfig
import com.example.eventapp.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)
    }
}