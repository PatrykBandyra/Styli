package styli.android.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import okhttp3.internal.toHexString
import styli.android.R
import styli.android.databinding.ActivityMainBinding
import styli.android.databinding.NavViewHeaderMenuBinding
import styli.android.global.AppPreferences
import styli.android.global.Constants
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class MainActivity : BaseActivity<ActivityMainBinding>(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navHeaderMenuBinding: NavViewHeaderMenuBinding
    private var backgroundColorInHex: String = Constants.DEFAULT_COLOR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavigationDrawers()

        navHeaderMenuBinding.tvUsername.text = AppPreferences.username

        binding.appBarMain.mainContent.btnColorPicker.setOnClickListener {
            openColorPicker()
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
        val dialog = AmbilWarnaDialog(this, Color.parseColor(backgroundColorInHex), object : OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                backgroundColorInHex =  color.toHexString().substring(2)
            }
            override fun onCancel(dialog: AmbilWarnaDialog?) {
                return
            }
        })
        dialog.show()
    }
}