package pl.agawrysiuk.imageupscalerwrapper.dto

data class ImageUpscalerWrapperResponse(
    val results: List<FileUpscalerResult>
)

data class FileUpscalerResult(
    val filePath: String,
    val success: Boolean,
    val message: String? = null,
)