package com.talearnt.configure;

import com.talearnt.enums.ErrorCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        // JWT 보안 사용 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // JWT 사용 등록
        Components components = new Components().addSecuritySchemes("bearerAuth", securityScheme);
        OpenAPI openApi = new OpenAPI().components(components)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(apiInfo());

        for (ErrorCode errorCode : ErrorCode.values()){
            openApi.getComponents().addResponses(errorCode.name(), errorCode.getApiResponse());
        }

        return openApi;
    }

    private Info apiInfo() {
        return new Info()
                .title("Talearnt API")
                .description("Talearnt 개발자의 API 소통구간 \n JWT 토큰 발급 ID test@test.com\n PWD : test")
                .version("1.0.0");

    }

}
