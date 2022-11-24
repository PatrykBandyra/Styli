package styli.android.activities

import android.os.Bundle
import styli.android.databinding.ActivityGalleryBinding

class GalleryActivity : BaseActivity<ActivityGalleryBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getViewBinding() = ActivityGalleryBinding.inflate(layoutInflater)
}