package com.example.clean_architecture_mvvm_news_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clean_architecture_mvvm_news_app.databinding.ItemArticlePreviewBinding
import com.example.clean_architecture_mvvm_news_app.models.Article
import com.example.clean_architecture_mvvm_news_app.utils.Constants.TYPE_SAVED_FRAGMENT
import com.example.clean_architecture_mvvm_news_app.utils.ImageLoader


class NewsAdapter(private val type: Int) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        if (type == TYPE_SAVED_FRAGMENT) {
            holder.binding.btnSave.visibility = View.GONE
        }


        val article = differ.currentList[position]




        holder.binding.apply {


            ImageLoader.loadImageWithGlide(ivArticleImage, article.urlToImage ?: "")



            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }

            }

            btnSave.setOnClickListener {
                onSaveButtonClickListener?.let {
                    it(article)
                }
            }

        }


    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setonItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private var onSaveButtonClickListener: ((Article) -> Unit)? = null
    fun setOnSaveButtonClickListener(listener: (Article) -> Unit) {
        onSaveButtonClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}