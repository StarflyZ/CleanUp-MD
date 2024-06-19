package com.capstone.cleanup.ui.adpter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.cleanup.R
import com.capstone.cleanup.data.Reports
import com.capstone.cleanup.databinding.ItemReportBinding
import com.capstone.cleanup.ui.detail.DetailReportActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ReportAdapter(
    option: FirestoreRecyclerOptions<Reports>
) : FirestoreRecyclerAdapter<Reports, ReportAdapter.ReportViewHolder>(option) {
    private val currentData = ArrayList<Reports>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportAdapter.ReportViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_report, parent, false)
        val binding = ItemReportBinding.bind(view)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ReportAdapter.ReportViewHolder,
        position: Int,
        model: Reports
    ) {
        holder.bind(model)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        val oldItem = currentData
        val newItem = snapshots
        val diffCallback = DiffCallback(oldItem, newItem)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        currentData.clear()
        currentData.addAll(snapshots)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Reports) {
            with(binding) {
                tvItemTitle.text = item.title
                tvItemName.text = item.reporter
                Glide.with(root)
                    .load(item.imgUrl)
                    .fitCenter()
                    .into(ivItemPhoto)

                if (item.timeStamp != null) {
                    val time = DateUtils.getRelativeTimeSpanString(item.timeStamp)
                    tvReportTime.text = "${item.location}, $time"
                }

                cardViewReport.setOnClickListener {
                    val intent = Intent(it.context, DetailReportActivity::class.java)
                    intent.putExtra(DetailReportActivity.EXTRA_NAME, item.reporter)
                    intent.putExtra(DetailReportActivity.EXTRA_TITLE, item.title)
                    intent.putExtra(DetailReportActivity.EXTRA_DESC, item.description)
                    intent.putExtra(DetailReportActivity.EXTRA_IMG, item.imgUrl)
                    intent.putExtra(DetailReportActivity.EXTRA_LOC, item.location)
                    intent.putExtra(DetailReportActivity.EXTRA_ID, item.id)
                    if (item.timeStamp != null) {
                        intent.putExtra(
                            DetailReportActivity.EXTRA_TIME_STAMP,
                            DateUtils.getRelativeTimeSpanString(item.timeStamp)
                        )
                    }
                    it.context.startActivity(intent)
                }
            }
        }
    }

    inner class DiffCallback(
        private val oldItem: List<Reports>,
        private val newItem: List<Reports>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItem.size

        override fun getNewListSize(): Int = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].title == newItem[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldItem[oldItemPosition]
            val new = newItem[newItemPosition]
            return old.title == new.title &&
                    old.reporter == new.reporter &&
                    old.description == new.description
        }

    }
}