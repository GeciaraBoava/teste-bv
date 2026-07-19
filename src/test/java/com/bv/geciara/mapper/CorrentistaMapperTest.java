package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.request.EnderecoRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import com.bv.geciara.model.enums.ETipoIdentificador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CorrentistaMapperTest {

    @Mock
    private ContaMapper contaMapper;

    @InjectMocks
    private CorrentistaMapper correntistaMapper;

    private Endereco endereco;
    private EnderecoRequest enderecoRequest;
    private Correntista correntista;

    @BeforeEach
    void setUp() {
        endereco = Endereco.builder()
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .uf("SP")
                .cep("01234567")
                .build();

        correntista = Correntista.builder()
                .id(1L)
                .nomeCompleto("Maria Silva")
                .endereco(endereco)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();
        correntista.setDataCadastro(LocalDateTime.of(2025, 1, 15, 10, 30));
    }

    @Test
    void toEntity_deveConverterTodosOsCampos() {
        CorrentistaRequest request = CorrentistaRequest.builder()
                .nomeCompleto("Maria Silva")
                .endereco(enderecoRequest)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        Correntista entity = correntistaMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Maria Silva", entity.getNomeCompleto());
        assertEquals(ETipoIdentificador.CPF, entity.getTipoIdentificador());
        assertEquals("12345678900", entity.getNumeroIdentificador());
        assertNotNull(entity.getEndereco());
        assertEquals("Rua das Flores", entity.getEndereco().getLogradouro());
    }


    @Test
    void toEntity_deveAceitarEnderecoNull() {
        CorrentistaRequest request = CorrentistaRequest.builder()
                .nomeCompleto("Maria Silva")
                .endereco(null)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        Correntista entity = correntistaMapper.toEntity(request);

        assertNotNull(entity);
        assertNull(entity.getEndereco());
    }

    @Test
    void toResumoResponse_deveConverterCamposCorretos() {
        CorrentistaResumoResponse resumo = correntistaMapper.toResumoResponse(correntista);

        assertNotNull(resumo);
        assertEquals(1L, resumo.getId());
        assertEquals("Maria Silva", resumo.getNomeCompleto());
        assertEquals(ETipoIdentificador.CPF, resumo.getTipoIdentificador());
        assertEquals("12345678900", resumo.getNumeroIdentificador());
    }

    @Test
    void toResponse_deveConverterTodosOsCampos() {
        ContaResponse contaResponse = ContaResponse.builder()
                .id(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .status(EStatusConta.ATIVA)
                .correntistaId(1L)
                .build();

        correntista.setContas(new ArrayList<>(List.of(
                com.bv.geciara.model.entities.Conta.builder()
                        .id(1L)
                        .numero("456789")
                        .agencia(1234)
                        .tipo(ETipoConta.CORRENTE)
                        .saldo(new BigDecimal("5000.00"))
                        .status(EStatusConta.ATIVA)
                        .correntista(correntista)
                        .build()
        )));

        org.mockito.Mockito.when(contaMapper.toResponse(
                org.mockito.ArgumentMatchers.any(com.bv.geciara.model.entities.Conta.class)))
                .thenReturn(contaResponse);

        CorrentistaResponse response = correntistaMapper.toResponse(correntista);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Maria Silva", response.getNomeCompleto());
        assertNotNull(response.getEndereco());
        assertEquals("Rua das Flores", response.getEndereco().getLogradouro());
        assertEquals(ETipoIdentificador.CPF, response.getTipoIdentificador());
        assertEquals("12345678900", response.getNumeroIdentificador());
        assertEquals(1, response.getContas().size());
        assertEquals("456789", response.getContas().get(0).getNumero());
    }

    @Test
    void toResponse_deveRetornarListaVazia_QuandoContasNull() {
        correntista.setContas(null);

        CorrentistaResponse response = correntistaMapper.toResponse(correntista);

        assertNotNull(response);
        assertNotNull(response.getContas());
        assertTrue(response.getContas().isEmpty());
    }

    @Test
    void toResponse_deveRetornarListaVazia_QuandoContasVazia() {
        correntista.setContas(new ArrayList<>());

        CorrentistaResponse response = correntistaMapper.toResponse(correntista);

        assertNotNull(response.getContas());
        assertTrue(response.getContas().isEmpty());
    }
}
