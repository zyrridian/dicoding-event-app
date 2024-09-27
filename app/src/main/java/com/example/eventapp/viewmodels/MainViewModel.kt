package com.example.eventapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventapp.data.response.EventResponse
import com.example.eventapp.data.response.ListEventsItem
import com.example.eventapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listUpcoming = MutableLiveData<List<ListEventsItem>?>()
    val listUpcoming: LiveData<List<ListEventsItem>?> = _listUpcoming

    private val _listFinished = MutableLiveData<List<ListEventsItem>?>()
    val listFinished: LiveData<List<ListEventsItem>?> = _listFinished

    companion object {
        private const val TAG = "UpcomingViewModel"
    }

    init {
        // Check if data is already loaded before making API calls
//        if (_listEvents.value == null) {
        findUpcoming()
//        }
//        if (_listFinished.value == null) {
        findFinished()
//        }
    }

    fun refreshData() {
        findUpcoming()
        findFinished()
    }

    fun findUpcoming() {
//        if (_listEvents.value != null) return // Skip if data already exists
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listUpcoming.value = responseBody.listEvents
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun findFinished() {
//        if (_listFinished.value != null) return // Skip if data already exists
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listFinished.value = responseBody.listEvents
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun searchEvents(active: Int, query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(active, query)
        if (active == 0) { // finished
            client.enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _listFinished.value = responseBody.listEvents
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            })
        } else if (active == 1) { // upcoming
            client.enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            _listUpcoming.value = responseBody.listEvents
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                }

            })
        }

    }

}