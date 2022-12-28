package styli.android.activities

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import styli.android.R
import styli.android.databinding.ActivityImageDetailsBinding
import styli.android.global.Constants.Activity.Gallery.IMAGE_POS
import styli.android.global.Constants.Activity.Gallery.IMAGE_URI
import java.io.File

class ImageDetailsActivity : BaseActivity<ActivityImageDetailsBinding>() {
    
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private var imagePos: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        imageUri = intent.getParcelableExtra(IMAGE_URI) as Uri?
        imageUri?.let { uri ->
            imageFile = uri.toFile()
            Glide.with(this)
                .asBitmap()
                .load(imageFile!!.readBytes())
                .placeholder(R.drawable.ic_image)
                .into(binding.ivImage)
        }
        imagePos = intent.getIntExtra(IMAGE_POS, 0)

        binding.btnDelete.setOnClickListener {

        }
        binding.btnDownload.setOnClickListener {

        }
    }

    override fun getViewBinding() = ActivityImageDetailsBinding.inflate(layoutInflater)
}