package pl.agawrysiuk.imageupscalerwrapper.validator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerData
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import org.springframework.util.ResourceUtils

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
    fun `validate should return errors for missing output folder`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("$INPUT_DIR/input1.txt", "/invalid/output")
            )
        )

        val result = validatorService.validate(request)

        assertEquals(1, result.size)
        assertEquals("Output folder does not exist: /invalid/output", result[0])
    }

    @Test
    fun `validate should return multiple errors for missing files and folders`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("/invalid/input1.jpg", "/invalid/output1"),
                ImageUpscalerData("/invalid/input2.jpg", "/invalid/output2")
            )
        )

        val result = validatorService.validate(request)

        assertEquals(4, result.size)
        assertEquals("Input file does not exist: /invalid/input1.jpg", result[0])
        assertEquals("Output folder does not exist: /invalid/output1", result[1])
        assertEquals("Input file does not exist: /invalid/input2.jpg", result[2])
        assertEquals("Output folder does not exist: /invalid/output2", result[3])
    }
}
