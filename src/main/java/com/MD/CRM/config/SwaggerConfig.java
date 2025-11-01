package com.MD.CRM.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customSwagger() {
        // final String cookieSchemeName = "cookieAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("CRM")
                        .version("1.0.0")
                        .description("API CRM Project"));
        // .addSecurityItem(new SecurityRequirement().addList(cookieSchemeName))
        // .components(new Components()
        //         .addSecuritySchemes(cookieSchemeName,
        //                 new SecurityScheme()
        //                         .name("access_token") // Tên cookie chứa JWT
        //                         .type(SecurityScheme.Type.APIKEY)
        //                         .in(SecurityScheme.In.COOKIE))

        // );
    }
}
