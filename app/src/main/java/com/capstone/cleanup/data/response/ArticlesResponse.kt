package com.capstone.cleanup.data.response

import com.google.gson.annotations.SerializedName

data class ArticlesResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("article")
	val article: List<ArticleItem>
)

data class ArticleItem(

	@field:SerializedName("imgurl")
	val imgurl: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
