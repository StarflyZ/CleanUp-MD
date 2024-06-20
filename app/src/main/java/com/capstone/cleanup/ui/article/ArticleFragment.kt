package com.capstone.cleanup.ui.article

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.FragmentArticleBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.adpter.ArticleAdapter

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding

    private val articleViewModel by viewModels<ArticleViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = findNavController()

        if (navController.currentDestination?.id == R.id.ReportFragment) {
            navController.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleAdapter = ArticleAdapter()
        val layoutManager = LinearLayoutManager(requireActivity())
        binding?.rvArticle?.layoutManager = layoutManager
        binding?.rvArticle?.adapter = articleAdapter

        articleViewModel.getArticles()

        articleViewModel.articles.observe(requireActivity()) {
            if (it != null) {
                articleAdapter.submitList(it)
                Log.d(TAG, "Article not null")
            }
        }

        articleViewModel.isLoading.observe(requireActivity()) {
            showLoading(it)
        }

        binding?.tvToReport?.setOnClickListener {
            navController.navigate(R.id.action_nav_article_to_ReportFragment)
        }

        articleViewModel.isLoading.observe(requireActivity()) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = ArticleFragment::class.java.simpleName
    }
}