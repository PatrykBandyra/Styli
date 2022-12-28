package styli.android.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import retrofit2.HttpException
import styli.android.R
import styli.android.api.HttpClient
import styli.android.databinding.ActivityImageDetailsBinding
import styli.android.global.Constants.Activity.Gallery.IMAGE_DELETED_RESULT
import styli.android.global.Constants.Activity.Gallery.IMAGE_ID
import styli.android.global.Constants.Activity.Gallery.IMAGE_POS
import styli.android.global.Constants.Activity.Gallery.IMAGE_URI
import java.io.File
import java.io.IOException
import java.util.*

class ImageDetailsActivity : BaseActivity<ActivityImageDetailsBinding>() {

    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private var imagePos: Int? = null
    private var imageId: Long? = null

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
        imageId = intent.getLongExtra(IMAGE_ID, 0)

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                if (HttpClient.api == null) {
                    startActivity(Intent(this@ImageDetailsActivity, SignInActivity::class.java))
                }
                val response = try {
                    HttpClient.api?.deleteImage(imageId!!)
                } catch (e: IOException) {
                    Log.e(
                        MainActivity.TAG,
                        "IOException, possible lack of Internet connection. ${e.message}"
                    )
                    showErrorSnackBar(R.string.check_your_internet_connection)
                    return@launch
                } catch (e: HttpException) {
                    Log.e(MainActivity.TAG, "HttpException, unexpected response. ${e.message}")
                    showErrorSnackBar(R.string.unexpected_http_response)
                    return@launch
                }
                if (response?.isSuccessful == true) {
                    val intent = Intent().apply {
                        putExtra(IMAGE_POS, imagePos)
                    }
                    setResult(IMAGE_DELETED_RESULT, intent)
                    finish()
                }
            }
        }
        binding.btnDownload.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val imagesUri =
                            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        val contentValues = ContentValues()
                        contentValues.put(
                            MediaStore.Images.Media.DISPLAY_NAME,
                            "${System.currentTimeMillis()}.jpg"
                        )
                        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*")
                        contentResolver.insert(imagesUri, contentValues)?.let { uri ->
                            val imageBitmap = BitmapFactory.decodeFile(imageFile!!.path)
                            contentResolver.openOutputStream(uri).use { outStream ->
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                                runOnUiThread {
                                    Toast.makeText(
                                        this@ImageDetailsActivity,
                                        getString(R.string.image_download_success),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationaleDialogForPermissions()
                }
            }).onSameThread().check()
        }
    }

    override fun getViewBinding() = ActivityImageDetailsBinding.inflate(layoutInflater)
}