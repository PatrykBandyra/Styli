package styli.image.dto

data class ImageDto(
    val id: Long,
    val image: ByteArray,
    val description: String?,
    val username: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageDto

        if (id != other.id) return false
        if (!image.contentEquals(other.image)) return false
        if (description != other.description) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }
}