package pl.agawrysiuk.imageupscalerwrapper.validator

import org.springframework.stereotype.Service
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Service
class RequestValidatorService {
    fun validate(request: ImageUpscalerWrapperRequest): List<String> {
        val validationResults = mutableListOf<String>()
        request.data.forEach {
            if (!File(it.inputFilePath).exists()) {
                validationResults.add("Input file does not exist: ${it.inputFilePath}")
            }
            if (!File(it.outputDirPath).exists()) {
                Files.createDirectories(Paths.get(it.outputDirPath))
            }
        }
        return validationResults
    }
}