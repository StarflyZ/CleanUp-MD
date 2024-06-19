package com.capstone.cleanup.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.Message
import com.capstone.cleanup.data.repository.MainRepository
import com.capstone.cleanup.ui.adpter.MessageAdapter
import com.capstone.cleanup.ui.adpter.ReportAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel(repository: MainRepository) : ViewModel() {
    private val reportRef = repository.reportRef
    val currentUser = repository.currentUser

    private var _messageRef = MutableLiveData<CollectionReference>()
    private val messageRef: LiveData<CollectionReference> get() = _messageRef

    private var jobDone = MutableLiveData<Boolean>()

    private var _messageAdapter = MutableLiveData<MessageAdapter>()
    val messageAdapter: LiveData<MessageAdapter> get() = _messageAdapter

    private var c = 0

    private fun getMessageRef() {
        if (c == 1) return
        viewModelScope.launch(Dispatchers.IO) {
            reportRef.get().addOnSuccessListener {
                val documents = it.documents
                for (doc in documents) {
                    if (doc.id == DetailReportActivity.ID) {
                        _messageRef.value = reportRef.document(doc.id).collection(COMMENT_CHILD)
                        jobDone.value = true
                    } else continue
                }
            }
            c += 1
        }
    }

    private fun getAdapter() =
        viewModelScope.launch(Dispatchers.IO) {
            val option = FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(messageRef.value!!.orderBy("timeStamp"), Message::class.java)
                .build()

            launch(Dispatchers.Main) {
                _messageAdapter.value = MessageAdapter(option, currentUser?.displayName)
            }
        }

    suspend fun initData() {
        getMessageRef()

        delay(500)
        if (jobDone.value == true) getAdapter() else initData()
    }

    fun sendComment(comment: Message) {
        messageRef.value?.document()?.set(comment)
    }

    companion object {
        private const val COMMENT_CHILD = "comment"
    }
}