package com.example.eventapp.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "events")
class EventEntity(
    @PrimaryKey val id: Int?,
    val name: String?,
    val summary: String?,
    val description: String?,
    val imageLogo: String?,
    val mediaCover: String?,
    val category: String?,
    val ownerName: String?,
    val cityName: String?,
    val quota: Int?,
    val registrants: Int?,
    val beginTime: String?,
    val endTime: String?,
    val link: String?,
    var isFavorite: Boolean?,
    var isUpcoming: Boolean?,
    var isFinished: Boolean?,
) : Parcelable