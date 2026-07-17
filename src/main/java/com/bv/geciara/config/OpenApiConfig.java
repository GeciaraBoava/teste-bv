package com.bv.geciara.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Cadastro de Correntistas")
                        .description("API REST desenvolvida para o teste técnico BV")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Geciara Boava")
                                .email("geciaracardoso@gmail.com")));
    }
}