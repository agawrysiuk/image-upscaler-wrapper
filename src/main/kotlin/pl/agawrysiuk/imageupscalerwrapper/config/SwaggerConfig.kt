package pl.agawrysiuk.imageupscalerwrapper.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): OpenAPI =
        OpenAPI()
            .info(
                Info().title("Image Upscaler Wrapper API")
                    .description("Wrapper for image upscaling applications")
                    .version("v1.0")
            )
}