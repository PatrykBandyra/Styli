export interface EffectRequest {
    effectName: string,
    effectParams: EffectParam[],
    image: File,
    image2?: File
}

export interface EffectParam {
    name: string,
    value: string
}
