package com.example.eventapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapp.databinding.FragmentFavoriteBinding
import com.example.eventapp.ui.adapters.VerticalAdapter
import com.example.eventapp.ui.viewmodels.MainViewModel
import com.example.eventapp.ui.viewmodels.ViewModelFactory
import com.example.eventapp.utils.Result

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var verticalAdapter: VerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        verticalAdapter = VerticalAdapter { events ->
            if (events.isFavorite == true) {
                viewModel.deleteEvents(events)
            } else {
                viewModel.saveEvents(events)
            }
        }

        // Initially, set loading state to show shimmer effect
        verticalAdapter.setLoadingState(true)

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            binding.progressBar.visibility = View.GONE
            verticalAdapter.setLoadingState(false)
            verticalAdapter.submitList(favoriteEvents)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = verticalAdapter
        }

        setupSearchView()

    }

    private fun setupSearchView() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                val query = searchView.text.toString()
                searchBar.setText(searchView.text)
                searchView.hide()
                viewModel.searchFavoriteEvents(query).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            updateUI(isLoading = true, isEmpty = false)
                        }

                        is Result.Success -> {
                            verticalAdapter.submitList(result.data)
                            updateUI(isLoading = false, isEmpty = result.data.isEmpty())
                        }

                        is Result.Error -> {
                            updateUI(isLoading = false, isEmpty = true)
                        }
                    }
                }
                false
            }
        }
    }

    private fun updateUI(isLoading: Boolean, isEmpty: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            noDataFoundLottie.visibility = if (isEmpty && !isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (!isEmpty && !isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}