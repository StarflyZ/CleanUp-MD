package com.capstone.cleanup.ui.post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.cleanup.data.Reports
import com.capstone.cleanup.data.repository.MainRepository

class PostViewModel(repository: MainRepository) : ViewModel() {
    val username = repository.currentUser?.displayName
    private val reportRef = repository.reportRef
    private val newReportRef = reportRef.document()

    private val storage = repository.storage
    private val articleDataRef = storage.reference.child(REPORT_DATA_REF).child(username!!).child(newReportRef.id)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _imgUrl = MutableLiveData<Uri>()
    val imgUrl: LiveData<Uri> get() = _imgUrl

    fun uploadPicAndGetUrl(imgUri: Uri) {
        val uploadTask = articleDataRef.putFile(imgUri)

        uploadTask.addOnProgressListener {
            _isLoading.value = true
        }

        uploadTask.addOnFailureListener {
            _isLoading.value = false
        }

        uploadTask.addOnSuccessListener {
            if (it.task.isSuccessful) {
                val resultURL = articleDataRef.downloadUrl

                resultURL.addOnSuccessListener { uri ->
                    _imgUrl.value = uri
                }
                _isLoading.value = false
            }
        }
    }

    fun addNewReport(report: Reports) {
        report.id = newReportRef.id
        _isLoading.value = true
        newReportRef.set(report).addOnCompleteListener {
            _isLoading.value = false
        }.addOnSuccessListener {
            _isSuccess.value = true
        }
    }

    companion object {
        private const val REPORT_DATA_REF = "report"
    }
}