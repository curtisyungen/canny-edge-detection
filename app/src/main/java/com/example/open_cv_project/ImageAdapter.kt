package com.example.open_cv_project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(private val context: Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private val imageCount = context.assets.list("")?.count { it.startsWith("image_") && it.endsWith(".png") } ?: 0
    private val croppedStates = BooleanArray(imageCount) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false) as LinearLayout
        return ImageViewHolder(itemView)
    }

    override fun getItemCount() = imageCount

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val isCropped = croppedStates[position]

        val imageBitmap = if (isCropped) {
            getCroppedImage(position)
        } else {
            getOriginalImage(position)
        }

        val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(imageBitmap)

        val button = holder.itemView.findViewById<Button>(R.id.button)
        button.text = if (isCropped) "Uncrop" else "Crop"
        button.setOnClickListener {
            val newState = !croppedStates[position]
            for (i in croppedStates.indices) {
                croppedStates[i] = false
            }
            croppedStates[position] = newState
            notifyDataSetChanged()
        }
    }

    private fun getCroppedImage(position: Int): Bitmap {
        val scaledBitmap = getScaledImage(position)
        return ImageUtils().cropEdges(scaledBitmap)
    }

    private fun getOriginalImage(position: Int): Bitmap {
        return getScaledImage(position)
    }

    private fun getScaledImage(position: Int): Bitmap {
        val assetName = "image_${position + 1}.png"
        val inputStream = context.assets.open(assetName)
        val src = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val screenWidth = context.resources.displayMetrics.widthPixels
        val scaledHeight = (src.height * (screenWidth / src.width.toFloat())).toInt()
        return src.scale(screenWidth, scaledHeight)
    }

    inner class ImageViewHolder(imageView: LinearLayout) : RecyclerView.ViewHolder(imageView)
}
