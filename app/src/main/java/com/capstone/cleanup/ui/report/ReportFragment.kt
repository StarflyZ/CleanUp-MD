package com.capstone.cleanup.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.cleanup.databinding.FragmentReportBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.adpter.ReportAdapter
import com.capstone.cleanup.ui.post.PostActivity

class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding

    private val reportViewModel by viewModels<ReportViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var reportAdapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportAdapter = reportViewModel.reportAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.fabAdd?.setOnClickListener {
            startActivity(Intent(requireActivity(), PostActivity::class.java))
        }

        val layoutManager = LinearLayoutManager(requireActivity())
        binding?.rvReport?.layoutManager = layoutManager

        binding?.rvReport?.adapter = reportAdapter
    }

    override fun onResume() {
        super.onResume()
        reportAdapter.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        reportAdapter.startListening()
    }
}