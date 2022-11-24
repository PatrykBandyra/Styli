package styli.android.utils

import android.util.Patterns

class Validators {
    companion object {
        fun String.isPasswordValid(): Boolean {
            return this.isNotBlank() && this.length in 8..24
        }
        fun String.isUsernameValid(): Boolean {
            return this.isNotBlank()
        }
        fun String.isEmailValid(): Boolean {
            return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
        }
        fun String.isNameValid(): Boolean {
            return this.isNotBlank()
        }
        fun String.isSurnameValid(): Boolean {
            return this.isNotBlank()
        }
    }
}