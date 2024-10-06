package com.example.eventapp.ui.activities

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.bumptech.glide.Glide
import com.example.eventapp.R
import com.example.eventapp.data.local.entity.EventEntity
import com.example.eventapp.databinding.ActivityDetailBinding
import com.example.eventapp.ui.viewmodels.MainViewModel
import com.example.eventapp.ui.viewmodels.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val customTabsIntent by lazy { CustomTabsIntent.Builder().build() }
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val event = intent.getParcelableExtra<EventEntity>("EXTRA_EVENT")
        event?.let { setupEventDetails(it) }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupEventDetails(event: EventEntity) {
        binding.apply {
            updateFavoriteIcon(event.isFavorite)

            titleTextView.text = event.name
            ownerTextView.text = getString(R.string.organized_by, event.ownerName)
            remainingQuotaTextView.text = getString(
                R.string.remaining_quota,
                event.quota?.minus(event.registrants ?: 0).toString()
            )
            beginTimeTextView.text = formatDate(event.beginTime)
            summaryTextView.text = event.summary
            descriptionTextView.apply {
                text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)
                movementMethod = LinkMovementMethod.getInstance()
            }
            Glide.with(this@DetailActivity).load(event.mediaCover).into(imageView)

            favoriteFab.setOnClickListener { toggleFavorite(event) }
            webpageFab.setOnClickListener { openWebPage(event.link) }
        }
    }

    private fun formatDate(dateString: String?): String? {
        return dateString?.let {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            inputFormat.parse(it)?.let { date -> outputFormat.format(date) }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean?) {
        binding.favoriteFab.setImageResource(
            if (isFavorite == true) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    private fun toggleFavorite(event: EventEntity) {
        event.isFavorite = !(event.isFavorite ?: false)
        updateFavoriteIcon(event.isFavorite)

        if (event.isFavorite == true) {
            viewModel.saveEvents(event)
        } else {
            viewModel.deleteEvents(event)
        }
    }

    private fun openWebPage(url: String?) {
        url?.let {
            customTabsIntent.launchUrl(this, Uri.parse(it))
        }
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
