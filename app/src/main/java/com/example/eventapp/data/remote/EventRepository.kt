package com.example.eventapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.eventapp.data.local.entity.EventEntity
import com.example.eventapp.data.local.room.EventDao
import com.example.eventapp.data.remote.response.EventResponse
import com.example.eventapp.data.remote.retrofit.ApiService
import com.example.eventapp.utils.Result

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {
    fun getEvents(active: Int): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(active = active)
            val events = response.listEvents
            val eventList = events?.map { event ->
                val isFavorite = event.name?.let { eventDao.isEventFavorite(it) }
                val isUpcoming = active == 1
                val isFinished = active == 0
                EventEntity(
                    event.id,
                    event.name,
                    event.summary,
                    event.description,
                    event.imageLogo,
                    event.mediaCover,
                    event.category,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.registrants,
                    event.beginTime,
                    event.endTime,
                    event.link,
                    isFavorite,
                    isUpcoming,
                    isFinished,
                )
            }
            eventDao.insertEvents(eventList)
        } catch (e: Exception) {
            Log.d("EventRepository", "getEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }

        // Save to room
        val localData: LiveData<Result<List<EventEntity>>> = if (active == 1) {
            eventDao.getUpcomingEvents().map { Result.Success(it) }
        } else {
            eventDao.getFinishedEvents().map { Result.Success(it) }
        }
        emitSource(localData)
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    suspend fun setFavoriteEvents(events: EventEntity, favorite: Boolean) {
        events.isFavorite = favorite
        eventDao.updateEvents(events)
    }

    fun searchEvents(active: Int, query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<List<EventEntity>>>
        try {
            localData = when (active) {
                1 -> {
                    eventDao.searchUpcomingEvents(query).map { Result.Success(it) }
                }
                0 -> {
                    eventDao.searchFinishedEvents(query).map { Result.Success(it) }
                }
                else -> {
                    eventDao.searchFavoriteEvents(query).map { Result.Success(it) }
                }
            }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getLatestEvent(): EventResponse? {
        return try {
            apiService.getEvents(active = -1, limit = 1)
        } catch (e: Exception) {
            Log.e("EventRepository", "Error fetching latest event: ${e.message}")
            null
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
        ): EventRepository = instance ?: synchronized(this) {
            instance ?: EventRepository(
                apiService,
                eventDao,
            )
        }.also { instance = it }
    }

}