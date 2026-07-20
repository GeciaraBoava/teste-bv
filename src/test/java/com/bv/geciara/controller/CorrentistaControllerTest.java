package com.bv.geciara.controller;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.request.EnderecoRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.exception.CorrentistaNaoEncontradoException;
import com.bv.geciara.exception.IdentificadorDuplicadoException;
import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.model.enums.ETipoIdentificador;
import com.bv.geciara.service.CorrentistaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CorrentistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CorrentistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CorrentistaService correntistaService;

    private CorrentistaResumoResponse resumoResponse;
    private CorrentistaResponse response;

    @BeforeEach
    void setUp() {
        EnderecoRequest enderecoRequest = new EnderecoRequest(
                "Rua das Flores",
                "123",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "01234567"
        );

        CorrentistaRequest request = new CorrentistaRequest(
                "Maria Silva",
                enderecoRequest,
                ETipoIdentificador.CPF,
                "12345678900"
        );

        resumoResponse = new CorrentistaResumoResponse(
                1L,
                "Maria Silva",
                ETipoIdentificador.CPF,
                "12345678900"
        );

        Endereco endereco = Endereco.builder()
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .uf("SP")
                .cep("01234567")
                .build();

        response = new CorrentistaResponse(
                1L,
                "Maria Silva",
                endereco,
                ETipoIdentificador.CPF,
                "12345678900",
                null,
                null
        );
    }

    @Test
    void listarTodos_deveRetornarListaComSucesso() throws Exception {
        when(correntistaService.listarTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(resumoResponse)));

        mockMvc.perform(get("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nomeCompleto", is("Maria Silva")));
    }

    @Test
    void listarTodos_deveRetornarListaVazia() throws Exception {
        when(correntistaService.listarTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void listarTodosCompletos_deveRetornarListaComSucesso() throws Exception {
        when(correntistaService.listarTodosCompletos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/correntistas/completos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].endereco.logradouro", is("Rua das Flores")));
    }

    @Test
    void buscarPorIdentificador_deveRetornarCorrentistaComSucesso() throws Exception {
        when(correntistaService.buscarPorIdentificador("12345678900")).thenReturn(response);

        mockMvc.perform(get("/api/correntistas/12345678900")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nomeCompleto", is("Maria Silva")))
                .andExpect(jsonPath("$.endereco.logradouro", is("Rua das Flores")));
    }

    @Test
    void buscarPorIdentificador_deveRetornar404_QuandoNaoEncontrado() throws Exception {
        when(correntistaService.buscarPorIdentificador("99999999999"))
                .thenThrow(new CorrentistaNaoEncontradoException("99999999999"));

        mockMvc.perform(get("/api/correntistas/99999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("99999999999")));
    }

    @Test
    void cadastrar_deveRetornar201_ComDadosValidos() throws Exception {
        when(correntistaService.cadastrar(any(CorrentistaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "endereco": {
                                        "logradouro": "Rua das Flores",
                                        "numero": "123",
                                        "bairro": "Centro",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "12345678900"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCompleto", is("Maria Silva")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void cadastrar_deveRetornar409_QuandoIdentificadorDuplicado() throws Exception {
        when(correntistaService.cadastrar(any(CorrentistaRequest.class)))
                .thenThrow(new IdentificadorDuplicadoException("CPF", "12345678900"));

        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "endereco": {
                                        "logradouro": "Rua das Flores",
                                        "numero": "123",
                                        "bairro": "Centro",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "12345678900"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", containsString("CPF")));
    }

    @Test
    void cadastrar_deveRetornar400_QuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "",
                                    "endereco": {
                                        "logradouro": "",
                                        "numero": "",
                                        "bairro": "",
                                        "cidade": "",
                                        "uf": "",
                                        "cep": ""
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", is("Erro de validação")))
                .andExpect(jsonPath("$.detalhes").isMap());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoEnderecoNulo() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "12345678900"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoCampoDesconhecido() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "endereco": {
                                        "logradouro": "Rua das Flores",
                                        "numero": "123",
                                        "bairro": "Centro",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "12345678900",
                                    "campoInvalido": "valor"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_deveRetornar400_QuandoTipoIdentificadorInvalido() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "endereco": {
                                        "logradouro": "Rua das Flores",
                                        "numero": "123",
                                        "bairro": "Centro",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "INVALIDO",
                                    "numeroIdentificador": "12345678900"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void atualizar_deveRetornar200_ComDadosValidos() throws Exception {
        when(correntistaService.atualizar(eq(1L), any(CorrentistaAtualizacaoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/correntistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva Santos"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", is("Maria Silva")));
    }

    @Test
    void atualizar_deveRetornar404_QuandoNaoEncontrado() throws Exception {
        when(correntistaService.atualizar(eq(99L), any(CorrentistaAtualizacaoRequest.class)))
                .thenThrow(new CorrentistaNaoEncontradoException(99L));

        mockMvc.perform(put("/api/correntistas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva Santos"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("99")));
    }

    @Test
    void atualizar_deveRejeitarBodyVazio() throws Exception {
        mockMvc.perform(put("/api/correntistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void excluir_deveRetornar204_ComSucesso() throws Exception {
        doNothing().when(correntistaService).excluir(1L);

        mockMvc.perform(delete("/api/correntistas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluir_deveRetornar404_QuandoNaoEncontrado() throws Exception {
        doThrow(new CorrentistaNaoEncontradoException(99L))
                .when(correntistaService).excluir(99L);

        mockMvc.perform(delete("/api/correntistas/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", containsString("99")));
    }
}
