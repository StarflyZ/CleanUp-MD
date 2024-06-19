package com.capstone.cleanup.ui.adpter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.cleanup.R
import com.capstone.cleanup.data.Message
import com.capstone.cleanup.databinding.ItemMessageBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MessageAdapter(
    option: FirestoreRecyclerOptions<Message>,
    private val currentUserName: String?
) : FirestoreRecyclerAdapter<Message, MessageAdapter.MessageViewHolder>(option) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.bind(model)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.apply {
                tvMessage.text = item.text
                setTextColor(item.name, tvMessage)
                tvMessenger.text = item.name
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .circleCrop()
                    .into(ivMessenger)
                if (item.timeStamp != null) {
                    tvTimestamp.text = DateUtils.getRelativeTimeSpanString(item.timeStamp)
                }
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_blue)
                binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_green)
            }
        }
    }
}