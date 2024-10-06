package com.example.eventapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapp.databinding.FragmentHomeBinding
import com.example.eventapp.ui.adapters.HorizontalAdapter
import com.example.eventapp.ui.viewmodels.MainViewModel
import com.example.eventapp.ui.adapters.VerticalAdapter
import com.example.eventapp.utils.Result
import com.example.eventapp.ui.viewmodels.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var verticalAdapter: VerticalAdapter
    private lateinit var horizontalAdapter: HorizontalAdapter

    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        horizontalAdapter = HorizontalAdapter {
            if (it.isFavorite == true) {
                viewModel.deleteEvents(it)
            } else {
                viewModel.saveEvents(it)
            }
        }

        verticalAdapter = VerticalAdapter {
            if (it.isFavorite == true) {
                viewModel.deleteEvents(it)
            } else {
                viewModel.saveEvents(it)
            }
        }

        // Initially, set loading state to show shimmer effect
        horizontalAdapter.setLoadingState(true)
        verticalAdapter.setLoadingState(true)

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    horizontalAdapter.setLoadingState(true)
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    horizontalAdapter.setLoadingState(false)
                    horizontalAdapter.submitList(result.data.take(5))
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
//                    Toast.makeText(context, "An error occurred" + result.error, Toast.LENGTH_SHORT)
//                        .show()
                }
            }
        }

        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    verticalAdapter.setLoadingState(true)
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    verticalAdapter.setLoadingState(false)
                    verticalAdapter.submitList(result.data.take(5))
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
//                    Toast.makeText(context, "An error occurred" + result.error, Toast.LENGTH_SHORT)
//                        .show()
                }
            }
        }

        binding.upcomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = horizontalAdapter
        }

        binding.finishedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = verticalAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}