package com.bv.geciara.service;

import com.bv.geciara.dto.request.ContaAtualizacaoRequest;
import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.exception.ContaNaoEncontradaException;
import com.bv.geciara.exception.CorrentistaNaoEncontradoException;
import com.bv.geciara.mapper.ContaMapper;
import com.bv.geciara.model.entities.Conta;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import com.bv.geciara.repository.ContaRepository;
import com.bv.geciara.repository.CorrentistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private ContaMapper contaMapper;

    @InjectMocks
    private ContaService contaService;

    private Correntista correntista;
    private Conta conta;
    private ContaRequest request;
    private ContaResponse response;

    @BeforeEach
    void setUp() {
        correntista = Correntista.builder()
                .id(1L)
                .nomeCompleto("Maria Silva")
                .tipoIdentificador(com.bv.geciara.model.enums.ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        conta = Conta.builder()
                .id(1L)
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .status(EStatusConta.ATIVA)
                .correntista(correntista)
                .build();

        request = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .build();

        response = ContaResponse.builder()
                .id(1L)
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .status(EStatusConta.ATIVA)
                .correntistaId(1L)
                .build();
    }

    @Test
    void cadastrar_deveCadastrarConta_QuandoDadosValidos() {
        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(contaMapper.toEntity(request, correntista)).thenReturn(conta);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(contaMapper.toResponse(conta)).thenReturn(response);

        ContaResponse resultado = contaService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals("456789", resultado.getNumero());
        verify(correntistaRepository).findById(1L);
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    void cadastrar_deveDefinirStatusPadraoATIVA_QuandoNaoInformado() {
        ContaRequest requestSemStatus = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .build();

        Conta contaSemStatus = Conta.builder()
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .correntista(correntista)
                .build();

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(contaMapper.toEntity(requestSemStatus, correntista)).thenReturn(contaSemStatus);
        when(contaRepository.save(any(Conta.class))).thenReturn(contaSemStatus);
        when(contaMapper.toResponse(contaSemStatus)).thenReturn(response);

        contaService.cadastrar(requestSemStatus);

        assertEquals(EStatusConta.ATIVA, contaSemStatus.getStatus());
    }

    @Test
    void cadastrar_deveLancarExcecao_QuandoCorrentistaNaoEncontrado() {
        when(correntistaRepository.findById(99L)).thenReturn(Optional.empty());

        ContaRequest requestInvalido = ContaRequest.builder()
                .correntistaId(99L)
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .build();

        assertThrows(CorrentistaNaoEncontradoException.class,
                () -> contaService.cadastrar(requestInvalido));
    }

    @Test
    void atualizar_deveAtualizarConta_QuandoDadosValidos() {
        ContaAtualizacaoRequest atualizacaoRequest = ContaAtualizacaoRequest.builder()
                .saldo(new BigDecimal("7500.00"))
                .build();

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(contaMapper.toResponse(conta)).thenReturn(response);

        ContaResponse resultado = contaService.atualizar(1L, atualizacaoRequest);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("7500.00"), conta.getSaldo());
        verify(contaRepository).save(conta);
    }

    @Test
    void atualizar_deveLancarExcecao_QuandoContaNaoEncontrada() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        ContaAtualizacaoRequest atualizacaoRequest = ContaAtualizacaoRequest.builder()
                .saldo(new BigDecimal("7500.00"))
                .build();

        assertThrows(ContaNaoEncontradaException.class,
                () -> contaService.atualizar(99L, atualizacaoRequest));
    }

    @Test
    void encerrar_deveAlterarStatusParaEncerrada() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);

        contaService.encerrar(1L);

        assertEquals(EStatusConta.ENCERRADA, conta.getStatus());
        verify(contaRepository).save(conta);
    }

    @Test
    void encerrar_deveLancarExcecao_QuandoContaNaoEncontrada() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class,
                () -> contaService.encerrar(99L));
    }

    @Test
    void encerrar_deveManterDadosOriginais_QuandoEncerrada() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);

        contaService.encerrar(1L);

        assertEquals(EStatusConta.ENCERRADA, conta.getStatus());
        assertEquals("456789", conta.getNumero());
        assertEquals(1234, conta.getAgencia());
        assertEquals("001", conta.getCodigoBanco());
        assertEquals(new BigDecimal("5000.00"), conta.getSaldo());
    }

    @Test
    void cadastrar_deveManterStatusInformado_QuandoFornecido() {
        ContaRequest requestComStatus = ContaRequest.builder()
                .correntistaId(1L)
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .build();

        Conta contaComStatus = Conta.builder()
                .numero("456789")
                .agencia(1234)
                .codigoBanco("001")
                .tipo(ETipoConta.CORRENTE)
                .saldo(new BigDecimal("5000.00"))
                .status(EStatusConta.BLOQUEADA)
                .correntista(correntista)
                .build();

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(contaMapper.toEntity(requestComStatus, correntista)).thenReturn(contaComStatus);
        when(contaRepository.save(any(Conta.class))).thenReturn(contaComStatus);
        when(contaMapper.toResponse(contaComStatus)).thenReturn(response);

        contaService.cadastrar(requestComStatus);

        assertEquals(EStatusConta.BLOQUEADA, contaComStatus.getStatus());
    }

    @Test
    void atualizar_deveAtualizarMultiplosCampos() {
        ContaAtualizacaoRequest multiRequest = ContaAtualizacaoRequest.builder()
                .numero("999999")
                .agencia(5678)
                .codigoBanco("237")
                .tipo(ETipoConta.POUPANCA)
                .saldo(new BigDecimal("10000.00"))
                .build();

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(contaMapper.toResponse(conta)).thenReturn(response);

        ContaResponse resultado = contaService.atualizar(1L, multiRequest);

        assertNotNull(resultado);
        assertEquals("999999", conta.getNumero());
        assertEquals(5678, conta.getAgencia());
        assertEquals("237", conta.getCodigoBanco());
        assertEquals(ETipoConta.POUPANCA, conta.getTipo());
        assertEquals(new BigDecimal("10000.00"), conta.getSaldo());
        verify(contaRepository).save(conta);
    }

    @Test
    void atualizar_deveManterCamposNaoInformados() {
        ContaAtualizacaoRequest vazioRequest = ContaAtualizacaoRequest.builder()
                .build();

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(contaMapper.toResponse(conta)).thenReturn(response);

        contaService.atualizar(1L, vazioRequest);

        assertEquals("456789", conta.getNumero());
        assertEquals(1234, conta.getAgencia());
        assertEquals("001", conta.getCodigoBanco());
        assertEquals(ETipoConta.CORRENTE, conta.getTipo());
        assertEquals(new BigDecimal("5000.00"), conta.getSaldo());
    }

    @Test
    void atualizar_deveAtualizarApenasAgencia() {
        ContaAtualizacaoRequest agenciaRequest = ContaAtualizacaoRequest.builder()
                .agencia(9999)
                .build();

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(contaMapper.toResponse(conta)).thenReturn(response);

        contaService.atualizar(1L, agenciaRequest);

        assertEquals(9999, conta.getAgencia());
        assertEquals("456789", conta.getNumero());
        verify(contaRepository).save(conta);
    }

    @Test
    void listarTodos_deveRetornarListaDeContas() {
        when(contaRepository.findAll()).thenReturn(List.of(conta));
        when(contaMapper.toResponse(conta)).thenReturn(response);

        List<ContaResponse> resultado = contaService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("456789", resultado.get(0).getNumero());
        verify(contaRepository).findAll();
    }

    @Test
    void listarTodos_deveRetornarListaVazia_QuandoNaoExistirContas() {
        when(contaRepository.findAll()).thenReturn(List.of());

        List<ContaResponse> resultado = contaService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(contaRepository).findAll();
    }
}
