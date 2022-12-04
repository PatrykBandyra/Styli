package styli.android.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.internal.toHexString
import retrofit2.HttpException
import styli.android.R
import styli.android.api.HttpClient
import styli.android.api.dto.effect.EffectParam
import styli.android.api.dto.effect.EffectRequest
import styli.android.api.dto.image.ImageDetails
import styli.android.databinding.ActivityMainBinding
import styli.android.databinding.NavViewHeaderMenuBinding
import styli.android.global.AppPreferences
import styli.android.global.Constants
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : BaseActivity<ActivityMainBinding>(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navHeaderMenuBinding: NavViewHeaderMenuBinding
    private var backgroundColorInHex: String = Constants.DEFAULT_COLOR
    private var effectInputType: String? = null
    private var selectedImageFileUri: Uri? = null
    private var selectedBgImageFileUri: Uri? = null
    private var currentImage: ByteArray? = null

    private val galleryImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.data != null) {
                    currentImage = null
                    selectedImageFileUri = result.data!!.data
                    try {
                        Glide
                            .with(this)
                            .load(selectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_image)
                            .into(binding.appBarMain.mainContent.ivImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private val galleryBgImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.data != null) {
                    selectedBgImageFileUri = result.data!!.data
                    try {
                        Glide
                            .with(this)
                            .load(selectedBgImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_image)
                            .into(binding.appBarMain.mainContent.ivBgImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private val cameraImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    currentImage = null
                    val imageUri = result.data!!.data
                    afterPictureTaken(imageUri)
                }
            }
        }

    private val cameraBgImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val imageUri = result.data!!.data
                    afterBgPictureTaken(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavigationDrawers()

        navHeaderMenuBinding.tvUsername.text = AppPreferences.username

        binding.appBarMain.mainContent.ibColor.setOnClickListener {
            openColorPicker()
        }

        binding.appBarMain.mainContent.actvEffectInputType.setOnItemClickListener { adapterView, _, position, _ ->
            val effectType = adapterView.getItemAtPosition(position).toString()
            if (effectType != effectInputType) {
                effectInputType = effectType
                changeUIBasedOnInputType()
            }
        }

        binding.appBarMain.mainContent.ivImage.setOnClickListener {
            showImageChooserDialog(galleryImageResultLauncher, cameraImageResultLauncher)
        }

        binding.appBarMain.mainContent.ivBgImage.setOnClickListener {
            showImageChooserDialog(galleryBgImageResultLauncher, cameraBgImageResultLauncher)
        }

        binding.appBarMain.mainContent.btnApply.setOnClickListener {
            applyEffect()
        }

        binding.appBarMain.mainContent.btnSave.setOnClickListener {
            uploadImage()
        }
    }

    override fun onResume() {
        super.onResume()
        showProgressDialog()
        lifecycleScope.launch {
            if (HttpClient.api == null) {
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            }
            val response = try {
                HttpClient.api?.getAvailableEffects()
            } catch (e: IOException) {
                Log.e(TAG, "IOException, possible lack of Internet connection. ${e.message}")
                hideProgressDialog()
                showErrorSnackBar(R.string.check_your_internet_connection)
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response. ${e.message}")
                hideProgressDialog()
                showErrorSnackBar(R.string.unexpected_http_response)
                return@launch
            }
            if (response?.isSuccessful == true && response.body() != null) {
                val availableEffects = response.body()!!
                val arrayAdapter = ArrayAdapter(
                    this@MainActivity,
                    R.layout.dropdown_effect_input_types,
                    availableEffects
                )
                binding.appBarMain.mainContent.actvEffectInputType.setAdapter(arrayAdapter)
            }
            hideProgressDialog()
        }
    }

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    private fun setUpNavigationDrawers() {
        val viewHeaderMenu = binding.navViewMenu.getHeaderView(0)
        navHeaderMenuBinding = NavViewHeaderMenuBinding.bind(viewHeaderMenu)

        binding.navViewMenu.setNavigationItemSelectedListener(this)
    }

    fun onMenuDrawerClicked(view: View) {
        toggleLeftDrawer(binding.drawerMenu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navGallery -> {
                val intent = Intent(this@MainActivity, GalleryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.navSignOut -> {
                AppPreferences.signOut()
                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerMenu.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openColorPicker() {
        val dialog = AmbilWarnaDialog(
            this,
            Color.parseColor(backgroundColorInHex),
            object : OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    backgroundColorInHex = "#${color.toHexString().substring(2)}"
                    binding.appBarMain.mainContent.ibColor.setBackgroundColor(
                        Color.parseColor(backgroundColorInHex)
                    )
                }

                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    return
                }
            })
        dialog.show()
    }

    private fun changeUIBasedOnInputType() {
        when (effectInputType) {
            Constants.Effects.CARTOONIZE -> {
                binding.appBarMain.mainContent.tvColor.visibility = View.GONE
                binding.appBarMain.mainContent.ibColor.visibility = View.GONE
                binding.appBarMain.mainContent.ivBgImage.visibility = View.GONE
                binding.appBarMain.mainContent.tvBgImage.visibility = View.GONE
            }
            Constants.Effects.BACKGROUND_SWAP -> {
                binding.appBarMain.mainContent.tvColor.visibility = View.VISIBLE
                binding.appBarMain.mainContent.ibColor.visibility = View.VISIBLE
                binding.appBarMain.mainContent.ivBgImage.visibility = View.VISIBLE
                binding.appBarMain.mainContent.tvBgImage.visibility = View.VISIBLE
            }
        }
    }

    private fun showImageChooserDialog(
        galleryResultLauncher: ActivityResultLauncher<Intent>,
        cameraResultLauncher: ActivityResultLauncher<Intent>
    ) {
        val imageDialog = AlertDialog.Builder(this)
        imageDialog.setTitle(R.string.select_action)
        val imageDialogItems = arrayOf(
            resources.getString(R.string.select_image_from_gallery),
            resources.getString(R.string.take_photo)
        )
        imageDialog.setItems(imageDialogItems) { _, which ->
            when (which) {
                0 -> chooseImageFromGallery(galleryResultLauncher)
                1 -> takePhotoFromCamera(cameraResultLauncher)
            }
        }
        imageDialog.show()
    }

    private fun chooseImageFromGallery(resultLauncher: ActivityResultLauncher<Intent>) {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent =
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                    resultLauncher.launch(galleryIntent)
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

    private fun takePhotoFromCamera(resultLauncher: ActivityResultLauncher<Intent>) {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    resultLauncher.launch(cameraIntent)
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

    private fun afterPictureTaken(imageUri: Uri?) {
        try {
            selectedImageFileUri = imageUri
            Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_image)
                .into(binding.appBarMain.mainContent.ivImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun afterBgPictureTaken(imageUri: Uri?) {
        try {
            selectedBgImageFileUri = imageUri
            Glide
                .with(this)
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_image)
                .into(binding.appBarMain.mainContent.ivBgImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun applyEffect() {
        if (effectInputType == null) {
            showErrorSnackBar(R.string.select_effect_first)
            return
        }
        if (selectedImageFileUri == null) {
            showErrorSnackBar(R.string.select_image_first)
            return
        }
        showProgressDialog()
        lifecycleScope.launch {
            if (HttpClient.api == null) {
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            }
            val image = if (currentImage != null) getImageFileFromByteArray(currentImage!!) else
                getImageFileFromUri(selectedImageFileUri!!)
            var bgImage: File? = null
            val bgColor: String?
            val effectParams = mutableListOf<EffectParam>()
            if (Constants.Effects.REQUIRES_BG_IMAGE_OR_COLOR.contains(effectInputType)) {
                bgImage =
                    if (selectedBgImageFileUri != null) getImageFileFromUri(selectedBgImageFileUri!!) else null
                bgColor = backgroundColorInHex
                effectParams.add(EffectParam("bg_color", bgColor))
            }
            val response = try {
                HttpClient.api?.applyEffect(
                    EffectRequest(
                        effectInputType!!,
                        effectParams
                    ),
                    MultipartBody.Part.createFormData("image", image!!.name, image.asRequestBody()),
                    bgImage?.asRequestBody()?.let {
                        MultipartBody.Part.createFormData("image2", bgImage.name, it)
                    }
                )
            } catch (e: IOException) {
                Log.e(TAG, "IOException, possible lack of Internet connection. ${e.message}")
                hideProgressDialog()
                showErrorSnackBar(R.string.check_your_internet_connection)
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response. ${e.message}")
                hideProgressDialog()
                showErrorSnackBar(R.string.unexpected_http_response)
                return@launch
            }
            if (response?.isSuccessful == true && response.body() != null) {
                currentImage = Base64.decode(response.body()!!.image, Base64.DEFAULT)
                Glide.with(this@MainActivity)
                    .load(currentImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image)
                    .into(binding.appBarMain.mainContent.ivImage)
            }
            hideProgressDialog()
        }
    }

    private fun uploadImage() {
        if (currentImage == null || selectedImageFileUri == null) {
            showErrorSnackBar(R.string.select_image_first)
            return
        }
        showProgressDialog()
        lifecycleScope.launch {
            if (HttpClient.api == null) {
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            }
            val image = if (currentImage != null) getImageFileFromByteArray(currentImage!!) else
                getImageFileFromUri(selectedImageFileUri!!)
            val response = try {
                HttpClient.api?.uploadImage(
                    ImageDetails(null),
                    MultipartBody.Part.createFormData(
                        "image",
                        image!!.name,
                        image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                )
            } catch (e: IOException) {
                Log.e(TAG, "IOException, possible lack of Internet connection. ${e.message}")
                hideProgressDialog()
                showErrorSnackBar(R.string.check_your_internet_connection)
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response. ${e.message}")
                hideProgressDialog()
                showErrorSnackBar(R.string.unexpected_http_response)
                return@launch
            }
            if (response?.isSuccessful == true) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.image_upload_success),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                showErrorSnackBar(R.string.image_upload_fail)
            }
            hideProgressDialog()
        }
    }

    private fun getImageFileFromByteArray(imageByteArray: ByteArray): File {
        val file = File(cacheDir, "currentImage.jpg")
        if (file.exists()) {
            file.delete()
        }
        FileOutputStream(file).use { outStream ->
            outStream.write(imageByteArray)
            return file
        }
    }

    private fun getImageFileFromUri(uri: Uri): File? {
        contentResolver.openFileDescriptor(uri, "r", null)?.use { fd ->
            val file = File(cacheDir, getFileName(uri) ?: "image.jpg")
            FileInputStream(fd.fileDescriptor).use { inStream ->
                FileOutputStream(file).use { outStream ->
                    inStream.copyTo(outStream)
                    return file
                }
            }
        }
        return null
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}