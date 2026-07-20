package com.bv.geciara.controller;

import com.bv.geciara.dto.request.ContaAtualizacaoRequest;
import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.exception.ContaNaoEncontradaException;
import com.bv.geciara.exception.CorrentistaNaoEncontradoException;
import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import com.bv.geciara.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService contaService;

    private ContaResponse response;

    @BeforeEach
    void setUp() {
        response = ContaResponse.builder()
                .id(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .status(EStatusConta.ATIVA)
                .correntistaId(1L)
                .build();
    }

    @Test
    void listarTodos_deveRetornar200_ComDados() throws Exception {
        when(contaService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numero", is("456789")))
                .andExpect(jsonPath("$[0].correntistaId", is(1)));
    }

    @Test
    void listarTodos_deveRetornar200_ListaVazia() throws Exception {
        when(contaService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void cadastrar_deveRetornar201_ComDadosValidos() throws Exception {
        when(contaService.cadastrar(any(ContaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "correntistaId": 1,
                                    "numero": "456789",
                                    "agencia": 1234,
                                    "tipo": "CORRENTE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero", is("456789")))
                .andExpect(jsonPath("$.correntistaId", is(1)))
                .andExpect(jsonPath("$.status", is("ATIVA")));
    }

    @Test
    void cadastrar_deveRetornar404_QuandoCorrentistaNaoEncontrado() throws Exception {
        when(contaService.cadastrar(any(ContaRequest.class)))
                .thenThrow(new CorrentistaNaoEncontradoException(99L));

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "correntistaId": 99,
                                    "numero": "456789",
                                    "agencia": 1234,
                                    "tipo": "CORRENTE"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", is("Correntista não encontrado com ID: 99")));
    }

    @Test
    void cadastrar_deveRetornar400_QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "correntistaId": null,
                                    "numero": "",
                                    "agencia": null,
                                    "tipo": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", is("Erro de validação")))
                .andExpect(jsonPath("$.detalhes").isMap());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoTipoInvalido() throws Exception {
        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "correntistaId": 1,
                                    "numero": "456789",
                                    "agencia": 1234,
                                    "tipo": "INVALIDO"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoCampoDesconhecido() throws Exception {
        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "correntistaId": 1,
                                    "numero": "456789",
                                    "agencia": 1234,
                                    "tipo": "CORRENTE",
                                    "campoInvalido": "valor"
                                }
                                """))
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
    void atualizar_deveRetornar200_ComDadosValidos() throws Exception {
        when(contaService.atualizar(eq(1L), any(ContaAtualizacaoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "saldo": 7500.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero", is("456789")));
    }

    @Test
    void atualizar_deveRetornar404_QuandoContaNaoEncontrada() throws Exception {
        when(contaService.atualizar(eq(99L), any(ContaAtualizacaoRequest.class)))
                .thenThrow(new ContaNaoEncontradaException(99L));

        mockMvc.perform(put("/api/contas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "saldo": 7500.00
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", is("Conta não encontrada com ID: 99")));
    }

    @Test
    void atualizar_deveRejeitarBodyVazio() throws Exception {
        mockMvc.perform(put("/api/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_deveRetornar400_QuandoCampoDesconhecido() throws Exception {
        mockMvc.perform(put("/api/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "saldo": 7500.00,
                                    "campoInvalido": "valor"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void encerrar_deveRetornar204_ComSucesso() throws Exception {
        doNothing().when(contaService).encerrar(1L);

        mockMvc.perform(delete("/api/contas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void encerrar_deveRetornar404_QuandoContaNaoEncontrada() throws Exception {
        doThrow(new ContaNaoEncontradaException(99L))
                .when(contaService).encerrar(99L);

        mockMvc.perform(delete("/api/contas/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", is("Conta não encontrada com ID: 99")));
    }
}
