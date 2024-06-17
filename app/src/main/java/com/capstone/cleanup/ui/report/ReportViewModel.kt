package com.capstone.cleanup.ui.report

import androidx.lifecycle.ViewModel
import com.capstone.cleanup.data.Reports
import com.capstone.cleanup.data.repository.MainRepository
import com.capstone.cleanup.ui.adpter.ReportAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class ReportViewModel(repository: MainRepository) : ViewModel() {
    private val reportRef = repository.reportRef.orderBy("timeStamp", Query.Direction.DESCENDING)
    private val option = FirestoreRecyclerOptions.Builder<Reports>()
        .setQuery(reportRef, Reports::class.java)
        .build()

    val reportAdapter = ReportAdapter(option)
}