package pl.agawrysiuk.imageupscalerwrapper.validator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerData
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import org.springframework.util.ResourceUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class RequestValidatorServiceTest {

    private val validatorService = RequestValidatorService()
    private val INPUT_DIR = ResourceUtils.getFile("classpath:input").absolutePath
    private val OUTPUT_DIR = ResourceUtils.getFile("classpath:output").absolutePath

    @Test
    fun `validate should return empty list when all paths exist`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("$INPUT_DIR/input1.txt", OUTPUT_DIR),
                ImageUpscalerData("$INPUT_DIR/input2.txt", OUTPUT_DIR)
            )
        )

        val result = validatorService.validate(request)

        assertEquals(0, result.size, "Validation should return no errors when all paths are valid.")
    }

    @Test
    fun `validate should return errors for missing input files`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("$INPUT_DIR/input3.txt", OUTPUT_DIR),
                ImageUpscalerData("$INPUT_DIR/input4.txt", OUTPUT_DIR)
            )
        )

        val result = validatorService.validate(request)

        assertEquals(2, result.size)
        assertEquals("Input file does not exist: $INPUT_DIR/input3.txt", result[0])
        assertEquals("Input file does not exist: $INPUT_DIR/input4.txt", result[1])
    }

    @Test
    fun `validate should create missing output folder`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("$INPUT_DIR/input1.txt", "$OUTPUT_DIR/output"),
            )
        )

        val result = validatorService.validate(request)

        assertEquals(0, result.size)
        assertTrue(File("$OUTPUT_DIR/output").exists())
        Files.delete(Paths.get("$OUTPUT_DIR/output"))
    }
}
