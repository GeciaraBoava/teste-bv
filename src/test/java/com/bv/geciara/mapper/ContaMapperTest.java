package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.model.entities.Conta;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import com.bv.geciara.model.enums.ETipoIdentificador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContaMapperTest {

    private ContaMapper contaMapper;

    private Correntista correntista;

    @BeforeEach
    void setUp() {
        contaMapper = new ContaMapper();

        correntista = Correntista.builder()
                .id(1L)
                .nomeCompleto("Maria Silva")
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();
    }

    @Test
    void toEntity_deveConverterTodosOsCampos() {
        ContaRequest request = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.CORRENTE)
                .build();

        Conta entity = contaMapper.toEntity(request, correntista);

        assertNotNull(entity);
        assertEquals("456789", entity.getNumero());
        assertEquals(1234, entity.getAgencia());
        assertEquals(ETipoConta.CORRENTE, entity.getTipo());
        assertEquals(BigDecimal.ZERO, entity.getSaldo());
        assertEquals(EStatusConta.ATIVA, entity.getStatus());
        assertEquals(correntista, entity.getCorrentista());
    }

    @Test
    void toEntity_deveVincularCorrentista() {
        ContaRequest request = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.POUPANCA)
                .build();

        Conta entity = contaMapper.toEntity(request, correntista);

        assertEquals(correntista.getId(), entity.getCorrentista().getId());
        assertEquals(ETipoConta.POUPANCA, entity.getTipo());
        assertEquals(BigDecimal.ZERO, entity.getSaldo());
    }

    @Test
    void toEntity_deveDefinirStatusPadraoATIVA() {
        ContaRequest request = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.CORRENTE)
                .build();

        Conta entity = contaMapper.toEntity(request, correntista);

        assertEquals(EStatusConta.ATIVA, entity.getStatus());
        assertEquals(BigDecimal.ZERO, entity.getSaldo());
    }

    @Test
    void toEntity_deveDefinirSaldoZero() {
        ContaRequest request = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.POUPANCA)
                .build();

        Conta entity = contaMapper.toEntity(request, correntista);

        assertEquals(BigDecimal.ZERO, entity.getSaldo());
    }

    @Test
    void toResponse_deveConverterTodosOsCampos() {
        LocalDateTime dataCadastro = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime dataAtualizacao = LocalDateTime.of(2025, 6, 20, 14, 0);

        Conta entity = Conta.builder()
                .id(1L)
                .numero("456789")
                .agencia(1234)
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .status(EStatusConta.ATIVA)
                .correntista(correntista)
                .build();
        entity.setDataCadastro(dataCadastro);
        entity.setDataAtualizacao(dataAtualizacao);

        ContaResponse response = contaMapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("456789", response.getNumero());
        assertEquals(1234, response.getAgencia());
        assertEquals(ETipoConta.CORRENTE, response.getTipo());
        assertEquals(new BigDecimal("5000.00"), response.getSaldo());
        assertEquals(EStatusConta.ATIVA, response.getStatus());
        assertEquals(1L, response.getCorrentistaId());
        assertEquals(dataCadastro, response.getDataCadastro());
        assertEquals(dataAtualizacao, response.getDataAtualizacao());
    }

    @Test
    void toResponse_deveExtrairCorrentistaId() {
        Correntista outroCorrentista = Correntista.builder()
                .id(42L)
                .nomeCompleto("João Santos")
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("98765432100")
                .build();

        Conta entity = Conta.builder()
                .id(2L)
                .numero("789012")
                .agencia(5678)
                .tipo(ETipoConta.POUPANCA)
                .saldo(new BigDecimal("2500.00"))
                .status(EStatusConta.BLOQUEADA)
                .correntista(outroCorrentista)
                .build();

        ContaResponse response = contaMapper.toResponse(entity);

        assertEquals(42L, response.getCorrentistaId());
        assertEquals(EStatusConta.BLOQUEADA, response.getStatus());
    }
}
