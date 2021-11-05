package com.example.clean_architecture_mvvm_news_app.domain.common

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.clean_architecture_mvvm_news_app.R

//Helper class for image loading
object ImageLoader {


    //Load image with Glide
    fun loadImageWithGlide(
        view: ImageView,
        url: String,
        placeholder: Int = R.drawable.ic_baseline_image_24
    ) {
        Glide.with(view)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .fallback(placeholder)
            .into(view)
    }
}