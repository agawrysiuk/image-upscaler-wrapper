package pl.agawrysiuk.imageupscalerwrapper.service

import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperResponse

interface FileUpscalerService {
    fun upscaleImages(request: ImageUpscalerWrapperRequest): ImageUpscalerWrapperResponse
}