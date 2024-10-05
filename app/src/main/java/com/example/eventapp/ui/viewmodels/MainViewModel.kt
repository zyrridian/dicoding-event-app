package com.example.eventapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.data.local.entity.EventEntity
import com.example.eventapp.data.remote.EventRepository
import com.example.eventapp.utils.Result
import kotlinx.coroutines.launch

class MainViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()
    fun getFinishedEvents() = eventRepository.getFinishedEvents()
    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()

    fun searchUpcomingEvents(query: String): LiveData<Result<List<EventEntity>>> =
        eventRepository.searchUpcomingEvents(query)

    fun searchFinishedEvents(query: String): LiveData<Result<List<EventEntity>>> =
        eventRepository.searchFinishedEvents(query)

    fun searchFavoriteEvents(query: String): LiveData<Result<List<EventEntity>>> =
        eventRepository.searchFavoriteEvents(query)

    fun saveEvents(events: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvents(events, true)
        }
    }

    fun deleteEvents(events: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvents(events, false)
        }
    }

}