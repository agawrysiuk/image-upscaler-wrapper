package pl.agawrysiuk.imageupscalerwrapper.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.util.ResourceUtils
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerData
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest

class RealESRGANFileUpscalerServiceTest {

    @Test
    fun `ensureBinaryExists should throw exception if binary does not exist`() {
        val nonExistentPath = "/path/to/nonexistent"
        val serviceWithNonExistentPath = RealESRGANFileUpscalerService(nonExistentPath)
        val request = mockk<ImageUpscalerWrapperRequest>()

        val exception = assertThrows<Exception> {
            serviceWithNonExistentPath.upscaleImages(request)
        }

        assertEquals(
            "Upscaler binary not found at configured path: $nonExistentPath",
            exception.message
        )
    }

    @Test
    fun `upscaleImage should handle process execution success`() {
        val upscalerBinaryPath = ResourceUtils.getFile("classpath:fake_upscaler").absolutePath
        val service = RealESRGANFileUpscalerService(upscalerBinaryPath)
        val data = ImageUpscalerData(
            inputFilePath = "/path/to/input/image.jpg",
            outputDirPath = "/path/to/output"
        )

        val mockProcess = mockk<Process>(relaxed = true)
        every { mockProcess.inputStream } returns "Processing completed".byteInputStream()
        every { mockProcess.waitFor() } returns 0

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockProcess

        val result = service.upscaleImages(ImageUpscalerWrapperRequest(listOf(data)))

        assertEquals(1, result.results.size)
        val singleResult = result.results.single()
        assertTrue(singleResult.success)
        assertEquals("/path/to/output/image.png", singleResult.filePath)
    }
}