package com.capstone.cleanup.ui.adpter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.cleanup.data.response.ArticleItem
import com.capstone.cleanup.databinding.ItemArticleBinding
import com.capstone.cleanup.ui.detail.DetailArticleActivity

class ArticleAdapter :
    ListAdapter<ArticleItem, ArticleAdapter.ArticleViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleAdapter.ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleAdapter.ArticleViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticleItem) {
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

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleItem>() {
            override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}