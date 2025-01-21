package pl.agawrysiuk.imageupscalerwrapper.validator

import org.springframework.stereotype.Service
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import java.io.File

@Service
class RequestValidatorService {
    fun validate(request: ImageUpscalerWrapperRequest): List<String> {
        val validationResults = mutableListOf<String>()
        request.data.forEach {
            if (!File(it.inputFilePath).exists()) {
                validationResults.add("Input file does not exist: ${it.inputFilePath}")
            }
            if (!File(it.outputDirPath).exists()) {
                validationResults.add("Output folder does not exist: ${it.outputDirPath}")
            }
        }
        return validationResults
    }
}