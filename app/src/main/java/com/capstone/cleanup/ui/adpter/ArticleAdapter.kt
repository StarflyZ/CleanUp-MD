package com.capstone.cleanup.ui.adpter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.cleanup.data.Articles
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.ItemArticleBinding
import com.capstone.cleanup.ui.detail.DetailArticleActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ArticleAdapter(
    option: FirestoreRecyclerOptions<Articles>
) : FirestoreRecyclerAdapter<Articles, ArticleAdapter.ArticleViewHolder>(option) {
    private val currentData = ArrayList<Articles>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleAdapter.ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_article, parent, false)
        val binding = ItemArticleBinding.bind(view)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ArticleAdapter.ArticleViewHolder,
        position: Int,
        model: Articles
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

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Articles) {
            with(binding) {
                tvItemTitle.text = item.title
                tvItemSource.text = item.source
                Glide.with(root)
                    .load(item.imgurl)
                    .fitCenter()
                    .into(ivItemPhoto)

                cardViewArticle.setOnClickListener {
                    val intent = Intent(it.context, DetailArticleActivity::class.java)
                    intent.putExtra(DetailArticleActivity.EXTRA_TITLE, item.title)
                    intent.putExtra(DetailArticleActivity.EXTRA_IMGURL, item.imgurl)
                    intent.putExtra(DetailArticleActivity.EXTRA_CONTENT, item.content)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    inner class DiffCallback(
        private val oldItem: List<Articles>,
        private val newItem: List<Articles>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItem.size

        override fun getNewListSize(): Int = newItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition].title == newItem[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldItem[oldItemPosition]
            val new = newItem[newItemPosition]
            return old.title == new.title && old.content == new.content
        }

    }
}