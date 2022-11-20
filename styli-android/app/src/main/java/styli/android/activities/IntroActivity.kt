package styli.android.activities

import android.content.Intent
import android.os.Bundle
import styli.android.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity<ActivityIntroBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    override fun getViewBinding() = ActivityIntroBinding.inflate(layoutInflater)

    override fun onBackPressed() {
        doubleBackToExit()
    }
}