package pl.agawrysiuk.imageupscalerwrapper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerData
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperRequest
import pl.agawrysiuk.imageupscalerwrapper.dto.ImageUpscalerWrapperResponse
import pl.agawrysiuk.imageupscalerwrapper.dto.FileUpscalerResult
import pl.agawrysiuk.imageupscalerwrapper.service.FileUpscalerService
import pl.agawrysiuk.imageupscalerwrapper.validator.RequestValidatorService

class ControllerTest {

    private val validatorService = mockk<RequestValidatorService>()
    private val fileUpscalerService = mockk<FileUpscalerService>()
    private val controller = Controller(validatorService, fileUpscalerService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    private val objectMapper = ObjectMapper()
        .registerModule(kotlinModule())

    @Test
    fun `upscaleImages should return response when validation passes`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("/valid/input1.jpg", "/valid/output"),
                ImageUpscalerData("/valid/input2.jpg", "/valid/output")
            )
        )
        val expectedResponse = ImageUpscalerWrapperResponse(
            listOf(
                FileUpscalerResult("/valid/output/input1.png", true),
                FileUpscalerResult("/valid/output/input2.png", true)
            )
        )

        every { validatorService.validate(request) } returns emptyList()
        every { fileUpscalerService.upscaleImages(request) } returns expectedResponse

        val response = mockMvc.perform(
            post("/api/upscale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andReturn().response

        assertEquals(200, response.status)
        assertEquals(
            """
            {"results":[{"filePath":"/valid/output/input1.png","success":true,"message":null},{"filePath":"/valid/output/input2.png","success":true,"message":null}]}
            """.trimIndent(),
            response.contentAsString
        )
    }

    @Test
    fun `upscaleImages should throw exception when validation fails`() {
        val request = ImageUpscalerWrapperRequest(
            data = listOf(
                ImageUpscalerData("/invalid/input1.jpg", "/valid/output"),
                ImageUpscalerData("/invalid/input2.jpg", "/valid/output")
            )
        )
        val validationErrors = listOf(
            "Input file does not exist: /invalid/input1.jpg",
            "Input file does not exist: /invalid/input2.jpg"
        )

        every { validatorService.validate(request) } returns validationErrors

        val exception = assertThrows(Exception::class.java) {
            mockMvc.perform(
                post("/api/upscale")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andReturn()
        }

        assertEquals(
            "Request processing failed: java.lang.Exception: The following requests are invalid:\nInput file does not exist: /invalid/input1.jpg\nInput file does not exist: /invalid/input2.jpg",
            exception.message
        )
    }
}
