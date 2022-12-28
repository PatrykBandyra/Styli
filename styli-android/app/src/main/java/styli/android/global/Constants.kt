package styli.android.global

object Constants {
    object Activity {
        const val REGISTRATION_SUCCESS = "registrationSuccess"

        object Gallery {
            const val PAGE_LOAD_BUFFER = 2
            const val PAGE_SIZE = 6

            const val IMAGE_DELETED_RESULT = 999

            const val IMAGE_ID = "imageId"
            const val IMAGE_URI = "imageUri"
            const val IMAGE_POS = "imagePos"
        }
    }

    object Effects {
        const val CARTOONIZE = "cartoonize"
        const val COLORIZE = "colorize"
        const val BACKGROUND_SWAP = "background-swap"

        val REQUIRES_BG_IMAGE_OR_COLOR = listOf(BACKGROUND_SWAP)
    }

    const val DEFAULT_COLOR = "#7400B8"
}