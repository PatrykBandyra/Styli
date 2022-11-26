package styli.android.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import styli.android.R
import styli.android.api.HttpClient
import styli.android.api.dto.auth.LoginForm
import styli.android.databinding.ActivitySignInBinding
import styli.android.global.AppPreferences
import styli.android.global.Constants
import styli.android.utils.Validators.Companion.isPasswordValid
import styli.android.utils.Validators.Companion.isUsernameValid
import java.io.IOException

class SignInActivity : BaseActivity<ActivitySignInBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfUserJustRegistered()
        setUpActionBarForReturnAction(
            binding.toolbarSignInActivity,
            icon = R.drawable.back_arrow_black
        )
        binding.btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    override fun getViewBinding() = ActivitySignInBinding.inflate(layoutInflater)

    private fun signInUser() {
        val username: String = binding.etUsername.text.toString().trim()
        val password: String = binding.etPassword.text.toString().trim()
        hideKeyboard()
        if (validateForm(username, password)) {
            showProgressDialog()
            lifecycleScope.launch {
                val response = try {
                    HttpClient.api?.login(LoginForm(username, password))
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
                    AppPreferences.username = username
                    AppPreferences.jwt = response.body()!!.token
                    AppPreferences.expiresAt = response.body()!!.expiresAt
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    finish()
                } else {
                    showErrorSnackBar(R.string.sign_in_error)
                }
                hideProgressDialog()
            }
        }
    }

    private fun validateForm(username: String, password: String): Boolean {
        return when {
            !username.isUsernameValid() -> {
                showErrorSnackBar(R.string.sign_in_username_error)
                false
            }
            !password.isPasswordValid() -> {
                showErrorSnackBar(R.string.sign_in_password_error)
                false
            }
            else -> {
                true
            }
        }
    }

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

    companion object {
        private const val TAG = "SignInActivity"
    }
}