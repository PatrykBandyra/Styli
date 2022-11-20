package styli.android.api.dto.effect

data class EffectRequest(
    val effectName: String,
    val effectParams: List<EffectParam>
)