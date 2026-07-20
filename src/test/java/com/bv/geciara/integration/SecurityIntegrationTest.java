package com.bv.geciara.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar401_SemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/correntistas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401_CredenciaisInvalidas() throws Exception {
        mockMvc.perform(get("/api/correntistas")
                        .with(httpBasic("admin", "senhaerrada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401_CredenciaisVazias() throws Exception {
        mockMvc.perform(get("/api/correntistas")
                        .with(httpBasic("", "")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar200_CredenciaisValidas() throws Exception {
        mockMvc.perform(get("/api/correntistas")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401_PostSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType("application/json")
                        .content("""
                                {
                                    "nomeCompleto": "Teste",
                                    "endereco": {
                                        "logradouro": "Rua A",
                                        "numero": "1",
                                        "bairro": "Centro",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "12345678900"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirActuatorHealth_ComAutenticacao() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401_DeleteSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/contas")
                        .contentType("application/json")
                        .content("""
                                {
                                    "correntistaId": 1,
                                    "numero": "456789",
                                    "agencia": 1234,
                                    "codigoBanco": "001",
                                    "tipo": "CORRENTE"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
