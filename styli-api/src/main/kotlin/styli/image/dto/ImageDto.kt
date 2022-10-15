package styli.image.dto

data class ImageDto(
    val image: ByteArray,
    val description: String?,
    val username: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageDto

        if (!image.contentEquals(other.image)) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image.contentHashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}