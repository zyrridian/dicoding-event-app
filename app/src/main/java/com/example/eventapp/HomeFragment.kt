package com.example.eventapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapp.databinding.FragmentHomeBinding
import com.example.eventapp.ui.HorizontalAdapter
import com.example.eventapp.ui.VerticalAdapter
import com.example.eventapp.ui.MainViewModel

class HomeFragment : Fragment(), MainActivity.NetworkChangeListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var verticalAdapter: VerticalAdapter
    private lateinit var horizontalAdapter: HorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAllRecyclerView()
        setupAllNetworkData()
    }

    private fun setupAllRecyclerView() {
        // Upcoming events
        horizontalAdapter = HorizontalAdapter(true)
        binding.upcomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = horizontalAdapter
        }
        // Finished Events
        verticalAdapter = VerticalAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = verticalAdapter
        }
    }

    private fun setupAllNetworkData() {
        viewModel.listUpcoming.observe(viewLifecycleOwner, Observer { listEvents ->
            listEvents?.let {
                // Stop shimmer and submit the data
                horizontalAdapter.setLoadingState(false)
                horizontalAdapter.submitList(it.take(5))
            }
        })
        viewModel.listFinished.observe(viewLifecycleOwner, Observer { listEvents ->
            listEvents?.let {
                // Stop shimmer and submit the data
                verticalAdapter.setLoadingState(false)
                verticalAdapter.submitList(it.take(5))
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Toggle shimmer effect
//            upcomingAdapter.setLoadingState(isLoading)
//            horizontalAdapter.setLoadingState(isLoading)
        })
//        viewModel.isEmpty.observe(viewLifecycleOwner, Observer { isEmpty ->
//            horizontalAdapter.setLoadingState(isEmpty)
//        })
    }

    override fun onNetworkChanged() {
//        horizontalAdapter.setLoadingState(true)
        viewModel.refreshData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            (context as? MainActivity)?.setOnDataRefreshListener(this)
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnDataRefreshListener")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}