package pl.agawrysiuk.imageupscalerwrapper.dto


data class ImageUpscalerWrapperRequest(
    val data: List<ImageUpscalerData>
)

data class ImageUpscalerData(
    val inputFilePath: String,
    val outputDirPath: String,
)
