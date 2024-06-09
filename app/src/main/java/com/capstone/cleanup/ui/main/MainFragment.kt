package com.capstone.cleanup.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.cleanup.databinding.FragmentMainBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.adpter.ArticleAdapter

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.isLoading.observe(requireActivity()) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binding?.rvArticle?.layoutManager = layoutManager

        adapter = mainViewModel.articleAdapter
        binding?.rvArticle?.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }
}