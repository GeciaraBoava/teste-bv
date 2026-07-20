package com.bv.geciara.integration;

import com.bv.geciara.repository.CorrentistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CorrentistaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @BeforeEach
    void setUp() {
        correntistaRepository.deleteAll();
    }

    @Test
    void fluxoCompleto_CRUD_Correntista() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Ana Souza",
                                    "endereco": {
                                        "logradouro": "Av Paulista",
                                        "numero": "1000",
                                        "bairro": "Bela Vista",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01310100"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "98765432100"
                                }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nomeCompleto", is("Ana Souza")));

        mockMvc.perform(get("/api/correntistas/98765432100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", is("Ana Souza")))
                .andExpect(jsonPath("$.numeroIdentificador", is("98765432100")))
                .andExpect(jsonPath("$.endereco.logradouro", is("Av Paulista")))
                .andExpect(jsonPath("$.endereco.uf", is("SP")))
                .andExpect(jsonPath("$.dataCadastro").isNotEmpty());

        mockMvc.perform(put("/api/correntistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Ana Souza Santos"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto", is("Ana Souza Santos")));

        mockMvc.perform(get("/api/correntistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/correntistas/completos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(delete("/api/correntistas/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/correntistas/98765432100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCadastro_ComDadosObrigatoriosFaltando() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "",
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", is("Erro de validação")))
                .andExpect(jsonPath("$.detalhes").isMap());
    }

    @Test
    void deveRejeitarCadastro_ComIdentificadorDuplicado() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Carlos Lima",
                                    "endereco": {
                                        "logradouro": "Rua Augusta",
                                        "numero": "500",
                                        "bairro": "Consolação",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01305000"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "11122233344"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Carlos Eduardo Lima",
                                    "endereco": {
                                        "logradouro": "Rua Augusta",
                                        "numero": "600",
                                        "bairro": "Consolação",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01305100"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "11122233344"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", containsString("CPF")));
    }

    @Test
    void deveRetornar404_AoBuscarCorrentistaInexistente() throws Exception {
        mockMvc.perform(get("/api/correntistas/99999999999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404_AoExcluirCorrentistaInexistente() throws Exception {
        mockMvc.perform(delete("/api/correntistas/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarCadastro_ComTipoIdentificadorInvalido() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "João Silva",
                                    "endereco": {
                                        "logradouro": "Rua A",
                                        "numero": "1",
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
    void deveCadastrarComComplemento_Opcional() throws Exception {
        mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Pedro Almeida",
                                    "endereco": {
                                        "logradouro": "Rua B",
                                        "numero": "200",
                                        "complemento": "Sala 5",
                                        "bairro": "Jardins",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "55566677788"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCompleto", is("Pedro Almeida")));
    }

    @Test
    void deveAtualizarEnderecoParcialmente() throws Exception {
        var resultado = mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Lucia Santos",
                                    "endereco": {
                                        "logradouro": "Rua C",
                                        "numero": "300",
                                        "bairro": "Centro",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "11122233311"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = ((Number) com.jayway.jsonpath.JsonPath.read(
                resultado.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(put("/api/correntistas/%d".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "endereco": {
                                        "logradouro": "Av. Brasil"
                                    }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.logradouro", is("Av. Brasil")))
                .andExpect(jsonPath("$.endereco.numero", is("300")))
                .andExpect(jsonPath("$.endereco.bairro", is("Centro")));
    }

    @Test
    void deveRejeitarBodyVazio_AoAtualizar() throws Exception {
        var resultado = mockMvc.perform(post("/api/correntistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Fernanda Lima",
                                    "endereco": {
                                        "logradouro": "Rua D",
                                        "numero": "400",
                                        "bairro": "Vila Mariana",
                                        "cidade": "São Paulo",
                                        "uf": "SP",
                                        "cep": "01234567"
                                    },
                                    "tipoIdentificador": "CPF",
                                    "numeroIdentificador": "99988877766"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = ((Number) com.jayway.jsonpath.JsonPath.read(
                resultado.getResponse().getContentAsString(), "$.id")).longValue();

        mockMvc.perform(put("/api/correntistas/%d".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
