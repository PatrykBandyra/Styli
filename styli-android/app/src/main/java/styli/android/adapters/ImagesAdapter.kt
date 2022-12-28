package styli.android.adapters

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import styli.android.R
import styli.android.api.dto.image.Content
import styli.android.databinding.ItemGalleryBinding

class ImagesAdapter(private val context: Context, var images: MutableList<Content>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    private inner class GalleryViewHolder(val binding: ItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        GalleryViewHolder(
            ItemGalleryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val galleryItem = images[position]
        if (holder is GalleryViewHolder) {
            Glide.with(context)
                .asBitmap()
                .load(Base64.decode(galleryItem.image, Base64.DEFAULT))
                .placeholder(R.drawable.ic_image)
                .into(holder.binding.ivItem)

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, galleryItem)
                }
            }
        }
    }

    override fun getItemCount(): Int = images.size

    fun interface OnClickListener {
        fun onClick(position: Int, image: Content)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}