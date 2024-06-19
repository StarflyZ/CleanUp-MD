package com.capstone.cleanup.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.cleanup.BuildConfig
import com.capstone.cleanup.R
import com.capstone.cleanup.data.Message
import com.capstone.cleanup.databinding.ActivityDetailReportBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.adpter.MessageAdapter
import com.capstone.cleanup.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class DetailReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReportBinding

    private var commentAdapter: MessageAdapter? = null

    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ID = intent.getStringExtra(EXTRA_ID)

        lifecycleScope.launch { detailViewModel.initData() }

        detailViewModel.messageAdapter.observe(this) {
            if (it != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "LiveData is not null")
                commentAdapter = it
            }
        }

        val reportImg = intent.getStringExtra(EXTRA_IMG)
        val name = intent.getStringExtra(EXTRA_NAME)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val desc = intent.getStringExtra(EXTRA_DESC)
        val timeStamp = intent.getStringExtra(EXTRA_TIME_STAMP)
        val loc = intent.getStringExtra(EXTRA_LOC)

        with(binding) {
            tvReportTitle.text = getString(R.string.judul_report, title, loc, timeStamp)
            tvReporter.text = name
            tvReportDetail.text = desc

            Glide.with(this@DetailReportActivity)
                .load(reportImg)
                .into(imageView)

            btnSend.setOnClickListener {
                val message = Message(
                    edMessage.text.toString(),
                    detailViewModel.currentUser?.displayName,
                    detailViewModel.currentUser?.photoUrl.toString(),
                    Date().time
                )

                detailViewModel.sendComment(message)
                edMessage.setText("")
                showToast(this@DetailReportActivity, "Message send")
                commentAdapter?.startListening()
            }
        }

        lifecycleScope.launch { setDelayedAdapter() }
    }

    private suspend fun setDelayedAdapter() {
        delay(1500)
        with(binding) {
            if (rvComment.adapter == null) {
                val layoutManager = LinearLayoutManager(this@DetailReportActivity)
                rvComment.layoutManager = layoutManager

                rvComment.adapter = commentAdapter
                if (BuildConfig.DEBUG) Log.d(TAG, "Adapter set")
                commentAdapter?.startListening()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        commentAdapter?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        commentAdapter?.stopListening()
    }

    companion object {
        private val TAG = DetailReportActivity::class.java.simpleName

        const val EXTRA_NAME = "extra name for report"
        const val EXTRA_IMG = "extra img for report"
        const val EXTRA_DESC = "extra desc for report"
        const val EXTRA_TIME_STAMP = "extra timeStamp for report"
        const val EXTRA_TITLE = "extra title for report"
        const val EXTRA_LOC = "extra location for report"

        const val EXTRA_ID = "extra id for report"

        var ID: String? = null
    }
}