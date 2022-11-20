package styli.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import styli.android.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val currentUserId = UserFireStore().getCurrentUserId()
                if (currentUserId.isNotEmpty()) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }
                finish()
            },
            1000
        )
    }

    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)
}