package styli.effects.dto.request

data class EffectRequest(
    val effectName: String,
    val effectParams: List<EffectParam>,
) {
    data class EffectParam(
        val name: String,
        val value: String
    )
}