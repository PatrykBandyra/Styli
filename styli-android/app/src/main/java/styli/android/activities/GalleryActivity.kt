package styli.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import kotlinx.coroutines.launch
import retrofit2.HttpException
import styli.android.R
import styli.android.adapters.ImagesAdapter
import styli.android.api.HttpClient
import styli.android.databinding.ActivityGalleryBinding
import styli.android.global.Constants.Activity.Gallery.IMAGE_DATA
import styli.android.global.Constants.Activity.Gallery.IMAGE_DELETED_RESULT
import styli.android.global.Constants.Activity.Gallery.IMAGE_POS
import styli.android.global.Constants.Activity.Gallery.PAGE_LOAD_BUFFER
import styli.android.global.Constants.Activity.Gallery.PAGE_SIZE
import java.io.IOException

class GalleryActivity : BaseActivity<ActivityGalleryBinding>() {

    private lateinit var imagesAdapter: ImagesAdapter
    private var isLoading: Boolean = false
    private var page: Int = 0
    private var isLastPage: Boolean = false

    private val imageDetailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == IMAGE_DELETED_RESULT) {
                val intent = result.data
                val imagePos = intent?.getIntExtra(IMAGE_POS, 0)
                imagePos?.let {
                    imagesAdapter.images.removeAt(imagePos)
                    imagesAdapter.notifyItemRemoved(imagePos)
                    imagesAdapter.notifyItemRangeChanged(imagePos, imagesAdapter.itemCount)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rvImages.layoutManager = GridLayoutManager(this, 2)
        imagesAdapter = ImagesAdapter(this@GalleryActivity, mutableListOf())
        initClickListener()
        binding.rvImages.adapter = imagesAdapter
        initScrollListener()

        loadImages()
    }

    override fun getViewBinding() = ActivityGalleryBinding.inflate(layoutInflater)

    @SuppressLint("NotifyDataSetChanged")
    private fun loadImages() {
        lifecycleScope.launch {
            if (HttpClient.api == null) {
                startActivity(Intent(this@GalleryActivity, SignInActivity::class.java))
            }
            isLoading = true
            val response = try {
                HttpClient.api?.getImagesPaginated(page, PAGE_SIZE)
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
            } finally {
                isLoading = false
            }
            if (response?.isSuccessful == true && response.body() != null) {
                isLastPage = response.body()!!.last
                binding.tvNoImages.visibility = View.GONE
                binding.rvImages.visibility = View.VISIBLE
                val posStart = imagesAdapter.itemCount
                val images = response.body()!!.content
                imagesAdapter.images.addAll(images)
                imagesAdapter.notifyItemRangeInserted(posStart, images.size)
            } else {
                binding.rvImages.visibility = View.GONE
                binding.tvNoImages.visibility = View.VISIBLE
            }
        }
    }

    private fun initClickListener() {
        imagesAdapter.setOnClickListener { pos, image ->
            val intent = Intent(this, ImageDetailsActivity::class.java)

            Log.e("HERE", image.image.length.toString())
            intent.putExtra(IMAGE_DATA, image)
            intent.putExtra(IMAGE_POS, pos)
            imageDetailsLauncher.launch(intent)
        }
    }

    private fun initScrollListener() {
        binding.rvImages.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = recyclerView.layoutManager!!.childCount
                val firstVisibleItemPos =
                    (recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                val totalItems = recyclerView.adapter!!.itemCount

                if (!isLastPage && !isLoading && ((visibleItemCount + firstVisibleItemPos - PAGE_LOAD_BUFFER) >= totalItems)) {
                    page++
                    loadImages()
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
}