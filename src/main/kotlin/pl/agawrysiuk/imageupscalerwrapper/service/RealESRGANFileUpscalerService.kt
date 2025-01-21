package pl.agawrysiuk.imageupscalerwrapper.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pl.agawrysiuk.imageupscalerwrapper.dto.FileUpscalerResult
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerData
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperResponse
import java.io.File
import java.io.IOException

@Service
class RealESRGANFileUpscalerService(
    @Value("\${upscaler.realesrgan.path}") private val upscalerBinaryPath: String,
) : FileUpscalerService {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun upscaleImages(request: ImageUpscalerWrapperRequest): ImageUpscalerWrapperResponse {
        ensureBinaryExists()
        val results = request.data.map { upscaleImage(it) }
        return ImageUpscalerWrapperResponse(results)
    }

    private fun ensureBinaryExists() {
        if (!File(upscalerBinaryPath).exists()) {
            throw Exception(
                "Upscaler binary not found at configured path: $upscalerBinaryPath"
            )
        }
    }

    private fun upscaleImage(data: ImageUpscalerData): FileUpscalerResult {
        val outputDir = File(data.outputDirPath)

        val outputFilePath = File(
            outputDir,
            getFileOutput(data)
        ).absolutePath

        val command = listOf(
            upscalerBinaryPath,
            "-i", data.inputFilePath,
            "-o", outputFilePath,
            "-n", "realesrgan-x4plus"
        )

        logger.info { "Executing command ${command.joinToString(" ")}" }

        return processImage(command, outputFilePath)
    }

    private fun getFileOutput(data: ImageUpscalerData): String {
        val fileName = data.inputFilePath.substring(data.inputFilePath.lastIndexOf('/') + 1)
        return fileName.substring(0, fileName.lastIndexOf('.')) + ".png"
    }

    private fun processImage(
        command: List<String>,
        outputFilePath: String
    ) = try {
        val process = ProcessBuilder(command)
            .directory(File(upscalerBinaryPath.substring(0, upscalerBinaryPath.lastIndexOf('/'))))
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        output.lines().forEach { logger.debug { it }}

        if (exitCode == 0) {
            FileUpscalerResult(
                filePath = outputFilePath,
                success = true
            )
        } else {
            FileUpscalerResult(
                filePath = outputFilePath,
                success = false,
                message = "Upscaling failed with exit code $exitCode. Output: $output"
            )
        }
    } catch (e: IOException) {
        FileUpscalerResult(
            filePath = outputFilePath,
            success = false,
            message = "Error executing upscaling process: ${e.message}"
        )
    }
}