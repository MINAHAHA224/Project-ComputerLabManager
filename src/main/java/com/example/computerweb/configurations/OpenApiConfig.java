package com.example.computerweb.configurations;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // setting securityScheme  , add cai thang scheme name = bearerAuth vao trong SecurityScheme
    // de khi ma ben @Operation() goi tơi @Operation(
    //    summary = "Get Home",
    //    security = @SecurityRequirement(name = "bearerAuth")
    //)
    // ==> va swagger khi minh setting .bearerFormat("JWT"))); thi no se tu hieu là
    // gui key = Authorization và value = Bearer <token>
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

}
