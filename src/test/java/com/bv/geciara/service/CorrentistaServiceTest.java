package com.bv.geciara.service;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.request.EnderecoRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.exception.CorrentistaNaoEncontradoException;
import com.bv.geciara.exception.IdentificadorDuplicadoException;
import com.bv.geciara.mapper.CorrentistaMapper;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.model.enums.ETipoIdentificador;
import com.bv.geciara.repository.CorrentistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrentistaServiceTest {

    @Mock
    private CorrentistaRepository correntistaRepository;

    @Mock
    private CorrentistaMapper correntistaMapper;

    @InjectMocks
    private CorrentistaService correntistaService;

    private Correntista correntista;
    private CorrentistaRequest request;
    private CorrentistaAtualizacaoRequest atualizacaoRequest;
    private CorrentistaResumoResponse resumoResponse;
    private CorrentistaResponse response;
    private Endereco endereco;
    private EnderecoRequest enderecoRequest;

    @BeforeEach
    void setUp() {
        enderecoRequest = new EnderecoRequest("Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01234567");

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

        request = new CorrentistaRequest("Maria Silva", enderecoRequest, ETipoIdentificador.CPF, "12345678900");

        atualizacaoRequest = new CorrentistaAtualizacaoRequest("Maria Silva Santos", null, null, null);

        resumoResponse = new CorrentistaResumoResponse(1L, "Maria Silva", ETipoIdentificador.CPF, "12345678900");

        response = new CorrentistaResponse(1L, "Maria Silva", endereco, ETipoIdentificador.CPF, "12345678900", null, null);
    }

    @Test
    void listarTodos_deveRetornarListaDeCorrentistas() {
        when(correntistaRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(correntista)));
        when(correntistaMapper.toResumoResponse(correntista)).thenReturn(resumoResponse);

        Page<CorrentistaResumoResponse> resultado = correntistaService.listarTodos(Pageable.unpaged());

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("Maria Silva", resultado.getContent().getFirst().nomeCompleto());
        verify(correntistaRepository).findAll(Pageable.unpaged());
    }

    @Test
    void listarTodos_deveRetornarListaVazia_QuandoNenhumCorrentista() {
        when(correntistaRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of()));

        Page<CorrentistaResumoResponse> resultado = correntistaService.listarTodos(Pageable.unpaged());

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarTodosCompletos_deveRetornarListaDeCorrentistasComContas() {
        when(correntistaRepository.findAllComContas(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(correntista)));
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        Page<CorrentistaResponse> resultado = correntistaService.listarTodosCompletos(Pageable.unpaged());

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("Maria Silva", resultado.getContent().getFirst().nomeCompleto());
        verify(correntistaRepository).findAllComContas(Pageable.unpaged());
    }

    @Test
    void listarTodosCompletos_deveRetornarListaVazia_QuandoNenhumCorrentista() {
        when(correntistaRepository.findAllComContas(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of()));

        Page<CorrentistaResponse> resultado = correntistaService.listarTodosCompletos(Pageable.unpaged());

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorIdentificador_deveRetornarCorrentista_QuandoEncontrado() {
        when(correntistaRepository.findByNumeroIdentificadorComContas("12345678900"))
                .thenReturn(Optional.of(correntista));
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.buscarPorIdentificador("12345678900");

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Maria Silva", resultado.nomeCompleto());
    }

    @Test
    void buscarPorIdentificador_deveLancarExcecao_QuandoNaoEncontrado() {
        when(correntistaRepository.findByNumeroIdentificadorComContas("99999999999"))
                .thenReturn(Optional.empty());

        CorrentistaNaoEncontradoException exception = assertThrows(
                CorrentistaNaoEncontradoException.class,
                () -> correntistaService.buscarPorIdentificador("99999999999"));

        assertTrue(exception.getMessage().contains("99999999999"));
    }

    @Test
    void cadastrar_deveCadastrarCorrentista_QuandoDadosValidos() {
        when(correntistaRepository.existsByNumeroIdentificador("12345678900")).thenReturn(false);
        when(correntistaMapper.toEntity(request)).thenReturn(correntista);
        when(correntistaRepository.save(any(Correntista.class))).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals("Maria Silva", resultado.nomeCompleto());
        verify(correntistaRepository).save(any(Correntista.class));
    }

    @Test
    void cadastrar_deveLancarExcecao_QuandoIdentificadorDuplicado() {
        when(correntistaRepository.existsByNumeroIdentificador("12345678900")).thenReturn(true);

        IdentificadorDuplicadoException exception = assertThrows(
                IdentificadorDuplicadoException.class,
                () -> correntistaService.cadastrar(request));

        assertTrue(exception.getMessage().contains("CPF"));
    }

    @Test
    void cadastrar_deveChamarExistsComIdentificadorDoRequest() {
        CorrentistaRequest requestFormatado = new CorrentistaRequest("Pedro Costa", enderecoRequest, ETipoIdentificador.CPF, "11122233344");

        Correntista pedro = Correntista.builder()
                .id(2L)
                .nomeCompleto("Pedro Costa")
                .endereco(endereco)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("11122233344")
                .build();

        CorrentistaResponse pedroResponse = new CorrentistaResponse(2L, "Pedro Costa", endereco, ETipoIdentificador.CPF, "11122233344", null, null);

        when(correntistaRepository.existsByNumeroIdentificador("11122233344")).thenReturn(false);
        when(correntistaMapper.toEntity(requestFormatado)).thenReturn(pedro);
        when(correntistaRepository.save(any(Correntista.class))).thenReturn(pedro);
        when(correntistaMapper.toResponse(pedro)).thenReturn(pedroResponse);

        CorrentistaResponse resultado = correntistaService.cadastrar(requestFormatado);

        assertNotNull(resultado);
        assertEquals("Pedro Costa", resultado.nomeCompleto());
        verify(correntistaRepository).existsByNumeroIdentificador("11122233344");
    }

    @Test
    void atualizar_deveAtualizarCorrentista_QuandoDadosValidos() {
        CorrentistaResponse responseAtualizado = new CorrentistaResponse(1L, "Maria Silva Santos", endereco, ETipoIdentificador.CPF, "12345678900", null, null);

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(correntistaRepository.save(correntista)).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(responseAtualizado);

        CorrentistaResponse resultado = correntistaService.atualizar(1L, atualizacaoRequest);

        assertNotNull(resultado);
        assertEquals("Maria Silva Santos", resultado.nomeCompleto());
        verify(correntistaRepository).save(correntista);
    }

    @Test
    void atualizar_deveLancarExcecao_QuandoCorrentistaNaoEncontrado() {
        when(correntistaRepository.findById(99L)).thenReturn(Optional.empty());

        CorrentistaNaoEncontradoException exception = assertThrows(
                CorrentistaNaoEncontradoException.class,
                () -> correntistaService.atualizar(99L, atualizacaoRequest));

        assertTrue(exception.getMessage().contains("99"));
    }

    @Test
    void atualizar_devePermitirAtualizacao_QuandoMesmoIdentificador() {
        CorrentistaAtualizacaoRequest mesmoIdRequest = new CorrentistaAtualizacaoRequest(null, null, ETipoIdentificador.CPF, "12345678900");

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(correntistaRepository.save(correntista)).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.atualizar(1L, mesmoIdRequest);

        assertNotNull(resultado);
        verify(correntistaRepository).save(correntista);
    }

    @Test
    void atualizar_deveLancarExcecao_QuandoNovoIdentificadorDuplicado() {
        CorrentistaAtualizacaoRequest reqNovoId = new CorrentistaAtualizacaoRequest(null, null, ETipoIdentificador.CPF, "98765432100");

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(correntistaRepository.existsByNumeroIdentificador("98765432100")).thenReturn(true);

        IdentificadorDuplicadoException exception = assertThrows(
                IdentificadorDuplicadoException.class,
                () -> correntistaService.atualizar(1L, reqNovoId));

        assertTrue(exception.getMessage().contains("CPF"));
    }

    @Test
    void atualizar_deveManterDataCadastro_QuandoAtualizado() {
        LocalDateTime dataCadastro = LocalDateTime.of(2025, 1, 15, 10, 30);
        correntista.setDataCadastro(dataCadastro);

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(correntistaRepository.save(correntista)).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        correntistaService.atualizar(1L, atualizacaoRequest);

        verify(correntistaRepository).save(correntista);
        assertEquals(dataCadastro, correntista.getDataCadastro());
    }

    @Test
    void excluir_deveExcluirCorrentista_QuandoEncontrado() {
        when(correntistaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(correntistaRepository).deleteById(1L);

        assertDoesNotThrow(() -> correntistaService.excluir(1L));
        verify(correntistaRepository).deleteById(1L);
    }

    @Test
    void excluir_deveLancarExcecao_QuandoNaoEncontrado() {
        when(correntistaRepository.existsById(99L)).thenReturn(false);

        CorrentistaNaoEncontradoException exception = assertThrows(
                CorrentistaNaoEncontradoException.class,
                () -> correntistaService.excluir(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(correntistaRepository, never()).deleteById(any());
    }
}
