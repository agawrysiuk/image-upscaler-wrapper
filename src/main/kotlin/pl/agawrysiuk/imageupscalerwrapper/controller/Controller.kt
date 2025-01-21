package pl.agawrysiuk.imageupscalerwrapper.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.agawrysiuk.imageupscalerwrapper.service.FileUpscalerService
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperResponse
import pl.agawrysiuk.imageupscalerwrapper.validator.RequestValidatorService

@RestController
@RequestMapping("/api")
class Controller(
    private val validatorService: RequestValidatorService,
    private val fileUpscalerService: FileUpscalerService,
) {

    @PostMapping("/upscale")
    fun upscaleImages(@RequestBody request: ImageUpscalerWrapperRequest): ImageUpscalerWrapperResponse {
        val validationResult = validatorService.validate(request)
        if (validationResult.isNotEmpty()) {
            throw Exception("The following requests are invalid:\n${validationResult.joinToString("\n")}")
        }
        return fileUpscalerService.upscaleImages(request)
    }
}
