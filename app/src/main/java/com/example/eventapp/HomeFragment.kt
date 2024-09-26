package com.example.eventapp

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
import com.example.eventapp.ui.UpcomingAdapter
import com.example.eventapp.ui.UpcomingViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpcomingViewModel by viewModels()
    private lateinit var upcomingAdapter: UpcomingAdapter
    private lateinit var horizontalAdapter: HorizontalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUpcomingRecyclerView()
        setupRecyclerView()

        viewModel.listEvents.observe(viewLifecycleOwner, Observer { listEvents ->
            listEvents?.let {
                horizontalAdapter.submitList(it.take(5))
            }
        })

        viewModel.listFinished.observe(viewLifecycleOwner, Observer { listEvents ->
            listEvents?.let {
                upcomingAdapter.submitList(it.take(5))
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

    }

    private fun setupUpcomingRecyclerView() {
        horizontalAdapter = HorizontalAdapter()
        binding.upcomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = horizontalAdapter
        }
    }

    private fun setupRecyclerView() {
        upcomingAdapter = UpcomingAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = upcomingAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}