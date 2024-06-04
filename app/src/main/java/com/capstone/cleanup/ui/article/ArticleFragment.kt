package com.capstone.cleanup.ui.article

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.FragmentArticleBinding
import com.capstone.cleanup.ui.report.ReportFragment

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding

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

        binding?.tvToReport?.setOnClickListener {
            navController.navigate(R.id.action_nav_article_to_ReportFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}