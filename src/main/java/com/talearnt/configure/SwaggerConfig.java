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
                .description("<h2>Talearnt 개발자의 API</h2>" +
                        " <strong>부기 : qnrl3442@gmail.com</strong> <br><br>" +
                        " <strong>정희 : junghee6859@gmail.com</strong> <br><br>" +
                        " <strong>제우 : dlwpdn1104@gmail.com</strong> <br><br>" +
                        " 공통 ID : test@test.com <br>" +
                        " <strong>공통 PWD : !1q2w3e4r</strong><br><br><br>" +
                        " <h4># 알림‼</h4>" +
                        " 위의 본인 아이디는 개발자 분들의 아이디 찾기를 할 수 있도록 휴대폰 번호로 만들어져 있습니다. <br>" +
                        " 만약 본인의 휴대폰 번호로 가입할 시 위의 아이디는 찾을 수 없고, 새로 가입한 아이디만 찾을 수 있습니다. <br>" +
                        " 문제 있는 상황이 아니오니, 오해하지 않으시길 바랍니다.<br>" +
                        " 이에 대한 의견이 있으신 분은 언제든 말씀해주세요.<br><br>" +
                        " <strong>문자 메세지 전송</strong>은 <strong>하루 총 50건</strong>만 가능합니다.<br>" +
                        " 그 이상 시도시 요금이 발생합니다. <br>" +
                        " 아직 요금을 추가하지 상태이므로, 메세지 전송이 실패했을 경우에 연락주시길 바랍니다.<br>")
                .version("1.0.0");

    }

}
