package com.example.eventapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.eventapp.data.local.entity.EventEntity
import com.example.eventapp.data.local.room.EventDao
import com.example.eventapp.data.remote.retrofit.ApiService
import com.example.eventapp.utils.AppExecutors
import com.example.eventapp.utils.Result

class EventRepository(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {

    private val result = MediatorLiveData<Result<List<EventEntity>>>()

    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(active = 1)
            val events = response.listEvents
            val eventList = events?.map { event ->
                val isFavorite = event.name?.let { eventDao.isEventFavorite(it) }
                val isUpcoming = true
                val isFinished = false
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
//            eventDao.deleteAll()
            eventDao.insertEvents(eventList)
        } catch (e: Exception) {
            Log.d("EventRepository", "getEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getUpcomingEvents().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(active = 0)
            val events = response.listEvents
            val eventList = events?.map { event ->
                val isFavorite = event.name?.let { eventDao.isEventFavorite(it) }
                val isUpcoming = false
                val isFinished = true
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
//            eventDao.deleteAll()
            eventDao.insertEvents(eventList)
        } catch (e: Exception) {
            Log.d("EventRepository", "getEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getFinishedEvents().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    suspend fun setFavoriteEvents(events: EventEntity, favorite: Boolean) {
        events.isFavorite = favorite
        eventDao.updateEvents(events)

    }

    fun searchUpcomingEvents(query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localData: LiveData<Result<List<EventEntity>>> =
                eventDao.searchUpcomingEvents(query).map { Result.Success(it) }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchFinishedEvents(query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localData: LiveData<Result<List<EventEntity>>> =
                eventDao.searchFinishedEvents(query).map { Result.Success(it) }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun searchFavoriteEvents(query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val localData: LiveData<Result<List<EventEntity>>> =
                eventDao.searchFavoriteEvents(query).map { Result.Success(it) }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository = instance ?: synchronized(this) {
            instance ?: EventRepository(
                apiService,
                eventDao,
                appExecutors
            )
        }.also { instance = it }
    }

}