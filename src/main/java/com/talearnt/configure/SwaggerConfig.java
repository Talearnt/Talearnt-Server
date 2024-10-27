package com.talearnt.configure;

import com.talearnt.enums.ErrorCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        Components components = new Components();
        OpenAPI openApi = new OpenAPI().components(components)
                .info(apiInfo());

        for (ErrorCode errorCode : ErrorCode.values()){
            openApi.getComponents().addResponses(errorCode.name(), errorCode.getApiResponse());
        }

        return openApi;
    }

    private Info apiInfo() {
        return new Info()
                .title("Talearnt API")
                .description("Talearnt 개발자의 API 소통구간")
                .version("1.0.0");
    }

}
