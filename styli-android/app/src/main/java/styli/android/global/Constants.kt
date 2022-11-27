package styli.android.global

object Constants {
    object Activity {
        const val REGISTRATION_SUCCESS = "registrationSuccess"
    }

    object Effects {
        const val CARTOONIZE = "cartoonize"
        const val BACKGROUND_SWAP = "background-swap"

        val REQUIRES_BG_IMAGE_OR_COLOR = listOf(BACKGROUND_SWAP)
    }

    const val DEFAULT_COLOR = "#7400B8"
}