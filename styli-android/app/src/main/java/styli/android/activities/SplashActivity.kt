package styli.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import styli.android.databinding.ActivitySplashBinding
import styli.android.global.AppPreferences

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.setup(applicationContext)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (isUserLoggedIn()) {
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

    companion object {
        private fun isUserLoggedIn(): Boolean {
            return AppPreferences.jwt != null &&
                    AppPreferences.username != null &&
                    AppPreferences.expiresAt != null &&
                    AppPreferences.expiresAt!! > (System.currentTimeMillis() / 1_000)
        }
    }
}