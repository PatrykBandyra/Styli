package styli.android.activities

import android.os.Bundle
import android.widget.Toast
import styli.android.databinding.ActivitySignInBinding
import styli.android.global.Constants

class SignInActivity : BaseActivity<ActivitySignInBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfUserJustRegistered()
    }

    override fun getViewBinding() = ActivitySignInBinding.inflate(layoutInflater)

    private fun checkIfUserJustRegistered() {
        val registrationSuccessMsg =
            intent.extras?.getString(Constants.Activity.REGISTRATION_SUCCESS)
        if (registrationSuccessMsg != null) {
            Toast.makeText(
                this@SignInActivity,
                registrationSuccessMsg,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}