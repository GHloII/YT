export interface videoDataFromBackend {
    title: string
    resolutions: string[]
    thumbnail: string
    sizeByQualityName: Record<string, number>
    idByQualityName: Record<string, number>
    audioId: number
}
