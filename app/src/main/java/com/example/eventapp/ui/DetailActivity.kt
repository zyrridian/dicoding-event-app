package com.example.eventapp.ui

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventapp.data.response.ListEventsItem
import com.example.eventapp.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val event = intent.getParcelableExtra<ListEventsItem>("EXTRA_EVENT")

        event?.let {
            binding.apply {
                titleTextView.text = event.name
                ownerTextView.text = "Organized by: ${event.ownerName}"
                remainingQuotaTextView.text = "Remaining quota: ${event.quota}"
                beginTimeTextView.text = formatDate(event.beginTime)
                summaryTextView.text = event.summary
                descriptionTextView.text =
                    Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)
                descriptionTextView.movementMethod = LinkMovementMethod.getInstance()
                Glide.with(this@DetailActivity)
                    .load(event.mediaCover)
                    .into(imageView)
            }
        }
    }

    private fun formatDate(dateString: String?): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = dateString?.let { inputFormat.parse(it) }
        return date?.let { outputFormat.format(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}