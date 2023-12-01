package com.project.heyboardgame.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.heyboardgame.R

class GlideUtils {
    companion object {
        fun loadThumbnailImage(
            context: Context,
            thumbnailUrl: String?,
            imageView: ImageView
        ) {
            if (thumbnailUrl == null) handleNullImageUrl(imageView)

            val originUrl = thumbnailUrl?.replace("/thumbnail/", "/origin/")
            Glide.with(context)
                .load(thumbnailUrl)
                .listener(getGlideListener(context, originUrl, imageView))
                .into(imageView)
        }

        private fun handleNullImageUrl(imageView: ImageView) {
            imageView.setImageResource(R.drawable.default_profile_img)
        }

        private fun getGlideListener(
            context: Context,
            altUrl: String?,
            imageView: ImageView
        ): RequestListener<Drawable> {
            return object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.post {
                        Glide.with(context)
                            .load(altUrl)
                            .error(R.drawable.default_profile_img)
                            .into(imageView)
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }
        }
    }
}