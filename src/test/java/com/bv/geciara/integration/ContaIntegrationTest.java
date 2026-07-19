package com.bv.geciara.integration;

import com.bv.geciara.dto.request.ContaAtualizacaoRequest;
import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import com.bv.geciara.model.enums.ETipoIdentificador;
import com.bv.geciara.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc(addFilters = false)
class ContaIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ContaRepository contaRepository;

    private MockMvc mockMvc;

    private Long correntistaId;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        var correntistaJson = """
                {
                    "nomeCompleto": "Maria Silva",
                    "endereco": {
                        "cep": "01234567",
                        "logradouro": "Rua A",
                        "numero": "123",
                        "complemento": "Apto 1",
                        "bairro": "Centro",
                        "cidade": "São Paulo",
                        "uf": "SP"
                    },
                    "tipoIdentificador": "CPF",
                    "numeroIdentificador": "12345678900"
                }
                """;

        var resultado = mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correntistaJson))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = resultado.getResponse().getContentAsString();
        correntistaId = ((Number) com.jayway.jsonpath.JsonPath.read(responseBody, "$.id")).longValue();
    }

    @Test
    void cadastrar_deveCadastrarConta_QuandoDadosValidos() throws Exception {
        var request = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """.formatted(correntistaId);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero", is("456789")))
                .andExpect(jsonPath("$.correntistaId", is(correntistaId.intValue())))
                .andExpect(jsonPath("$.status", is("ATIVA")))
                .andExpect(jsonPath("$.saldo", is(0)));
    }

    @Test
    void cadastrar_deveDefinirStatusPadraoATIVA_QuandoDadosValidos() throws Exception {
        var request = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """.formatted(correntistaId);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("ATIVA")));
    }

    @Test
    void cadastrar_deveDefinirSaldoZero() throws Exception {
        var request = """
                {
                    "correntistaId": %d,
                    "numero": "789012",
                    "agencia": 5678,
                    "tipo": "POUPANCA"
                }
                """.formatted(correntistaId);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo", is(0)))
                .andExpect(jsonPath("$.tipo", is("POUPANCA")));
    }

    @Test
    void cadastrar_deveRetornar404_QuandoCorrentistaInexistente() throws Exception {
        var request = """
                {
                    "correntistaId": 99999,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """;

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoDadosInvalidos() throws Exception {
        var request = """
                {
                    "correntistaId": null,
                    "numero": "",
                    "agencia": null,
                    "tipo": null
                }
                """;

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoTipoInvalido() throws Exception {
        var request = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "INVALIDO"
                }
                """.formatted(correntistaId);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoBodyVazio() throws Exception {
        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_deveAtualizarConta_QuandoDadosValidos() throws Exception {
        var contaJson = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """.formatted(correntistaId);

        var resultadoConta = mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contaJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long contaId = ((Number) com.jayway.jsonpath.JsonPath.read(
                resultadoConta.getResponse().getContentAsString(), "$.id")).longValue();

        var atualizacaoRequest = """
                {
                    "saldo": 7500.00,
                    "agencia": 5678
                }
                """;

        mockMvc.perform(put("/api/contas/%d".formatted(contaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizacaoRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo", is(7500.00)))
                .andExpect(jsonPath("$.agencia", is(5678)));
    }

    @Test
    void atualizar_deveAceitarBodyVazio() throws Exception {
        var contaJson = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """.formatted(correntistaId);

        var resultadoConta = mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contaJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long contaId = ((Number) com.jayway.jsonpath.JsonPath.read(
                resultadoConta.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(put("/api/contas/%d".formatted(contaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero", is("456789")))
                .andExpect(jsonPath("$.agencia", is(1234)));
    }

    @Test
    void atualizar_deveManterCamposNaoInformados() throws Exception {
        var contaJson = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """.formatted(correntistaId);

        var resultadoConta = mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contaJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long contaId = ((Number) com.jayway.jsonpath.JsonPath.read(
                resultadoConta.getResponse().getContentAsString(), "$.id")).longValue();

        var atualizacaoRequest = """
                {
                    "agencia": 9999
                }
                """;

        mockMvc.perform(put("/api/contas/%d".formatted(contaId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizacaoRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agencia", is(9999)))
                .andExpect(jsonPath("$.numero", is("456789")))
                .andExpect(jsonPath("$.tipo", is("CORRENTE")));
    }

    @Test
    void atualizar_deveRetornar404_QuandoContaNaoEncontrada() throws Exception {
        mockMvc.perform(put("/api/contas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "saldo": 100.00
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void encerrar_deveAlterarStatusParaEncerrada() throws Exception {
        var contaJson = """
                {
                    "correntistaId": %d,
                    "numero": "456789",
                    "agencia": 1234,
                    "tipo": "CORRENTE"
                }
                """.formatted(correntistaId);

        var resultadoConta = mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contaJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long contaId = ((Number) com.jayway.jsonpath.JsonPath.read(
                resultadoConta.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(delete("/api/contas/%d".formatted(contaId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        var contaAtualizada = contaRepository.findById(contaId);
        org.junit.jupiter.api.Assertions.assertTrue(contaAtualizada.isPresent());
        org.junit.jupiter.api.Assertions.assertEquals(EStatusConta.ENCERRADA,
                contaAtualizada.get().getStatus());
    }

    @Test
    void encerrar_deveRetornar404_QuandoContaNaoEncontrada() throws Exception {
        mockMvc.perform(delete("/api/contas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
