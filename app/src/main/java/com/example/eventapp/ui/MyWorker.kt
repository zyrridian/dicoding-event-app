package com.example.eventapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.eventapp.R
import com.example.eventapp.data.remote.EventRepository
import com.example.eventapp.di.Injection
import java.text.SimpleDateFormat
import java.util.Locale

class MyWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val EXTRA_EVENT = "event"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_1"
        const val CHANNEL_NAME = "event channel"
    }

    private val repository: EventRepository = Injection.provideRepository(context)

    override suspend fun doWork(): Result {
        return try {
            val latestEventResponse = repository.getLatestEvent()
            if (latestEventResponse != null && latestEventResponse.listEvents?.isNotEmpty() == true) {
                val name = latestEventResponse.listEvents.firstOrNull()?.name
                val date = latestEventResponse.listEvents.firstOrNull()?.beginTime
                val imageUrl = latestEventResponse.listEvents.firstOrNull()?.mediaCover
                val link = latestEventResponse.listEvents.firstOrNull()?.link
                val formattedDate = date?.let { formatEventDate(it) }
                val title = "Event Reminder: $formattedDate"
                val description = "$name."
                if (imageUrl != null && link != null) {
                    showNotification(title, description, imageUrl, link)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching the latest event: ${e.message}")
            Result.failure()
        }
    }

    private fun showNotification(
        title: String,
        description: String,
        imageUrl: String,
        link: String
    ) {

        // Notification click
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Load the image from the URL using Glide
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(BigPictureStyle().bigPicture(resource))
                        .setDefaults(NotificationCompat.DEFAULT_ALL)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        notification.setChannelId(CHANNEL_ID)
                        notificationManager.createNotificationChannel(channel)
                    }

                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun formatEventDate(dateString: String): String {
        // Parse the input date string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        // Format the date to your desired output format
        val outputFormat = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

}