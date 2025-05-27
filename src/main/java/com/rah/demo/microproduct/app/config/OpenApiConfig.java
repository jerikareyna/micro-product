package com.rah.demo.microproduct.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info=@Info(
                title = "Product CRUD",version = "1.0.0",description = "this is a especially crud"
        )
)

public class OpenApiConfig {
}
