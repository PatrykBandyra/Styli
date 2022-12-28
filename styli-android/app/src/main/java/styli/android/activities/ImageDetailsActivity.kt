package styli.android.activities

import android.os.Bundle
import android.util.Base64
import com.bumptech.glide.Glide
import styli.android.R
import styli.android.api.dto.image.Content
import styli.android.databinding.ActivityImageDetailsBinding
import styli.android.global.Constants.Activity.Gallery.IMAGE_DATA
import styli.android.global.Constants.Activity.Gallery.IMAGE_POS

class ImageDetailsActivity : BaseActivity<ActivityImageDetailsBinding>() {

    private var content: Content? = null
    private var imagePos: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        content = intent.getParcelableExtra(IMAGE_DATA) as Content?
        imagePos = intent.getIntExtra(IMAGE_POS, 0)

        Glide.with(this)
            .asBitmap()
            .load(Base64.decode(content?.image, Base64.DEFAULT))
            .placeholder(R.drawable.ic_image)
            .into(binding.ivImage)

        binding.btnDelete.setOnClickListener {

        }
        binding.btnDownload.setOnClickListener {

        }
    }

    override fun getViewBinding() = ActivityImageDetailsBinding.inflate(layoutInflater)
}