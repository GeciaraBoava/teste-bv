package com.bv.geciara.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Cadastro de Correntistas")
                        .description("""
                                API REST para gerenciamento (CRUD) de correntistas e contas de uma instituição financeira.
                                
                                ## Funcionalidades
                                - Listar todos os correntistas (resumo e completo)
                                - Buscar correntista por identificador (CPF/CNPJ/RG/Passaporte)
                                - Cadastrar, atualizar e excluir correntistas
                                - Cadastrar, atualizar e encerrar contas vinculadas a correntistas
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Geciara Boava")
                                .email("geciaracardoso@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}