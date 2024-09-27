package com.example.eventapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.eventapp.data.response.ListEventsItem
import com.example.eventapp.databinding.ItemNormalHorizontalBinding
import com.example.eventapp.databinding.ItemShimmerHorizontalBinding
import com.example.eventapp.ui.activities.DetailActivity

class HorizontalAdapter(private var isLoading: Boolean = true) :
    ListAdapter<ListEventsItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val binding = ItemShimmerHorizontalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ShimmerViewHolder(binding)
        } else {
            val binding = ItemNormalHorizontalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            MyViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder && !isLoading) {
            val event = getItem(position)
            holder.bind(event)
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) 5 else super.getItemCount()
    }

    class ShimmerViewHolder(binding: ItemShimmerHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    class MyViewHolder(private val binding: ItemNormalHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.titleTextView.text = event.name
            binding.summaryTextView.text = event.summary
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .transform(RoundedCorners(16))
                .into(binding.imageView)
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_EVENT", event)
                binding.root.context.startActivity(intent)
            }
        }
    }

    companion object {

        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_DATA = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {

            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLoadingState(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

}
