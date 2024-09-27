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
import com.example.eventapp.databinding.FragmentFinishedBinding
import com.example.eventapp.ui.VerticalAdapter
import com.example.eventapp.ui.MainViewModel

class FinishedFragment : Fragment(), MainActivity.NetworkChangeListener {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var verticalAdapter: VerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()
        setupRecyclerView()
        setupAllNetworkData()
    }

    private fun setupAllNetworkData() {
        viewModel.listFinished.observe(viewLifecycleOwner, Observer { listEvents ->
            listEvents?.let {
                verticalAdapter.setLoadingState(false)
                verticalAdapter.submitList(it)
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun setupSearchView() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    val query = searchView.text.toString()
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    if (query.isNotEmpty()) {
                        viewModel.searchEvents(0, query)
                    } else {
                        viewModel.findFinished()
                    }
                    false
                }
        }
    }

    private fun setupRecyclerView() {
        verticalAdapter = VerticalAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = verticalAdapter
        }
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