package com.bv.geciara.service;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
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

    @BeforeEach
    void setUp() {
        Endereco endereco = Endereco.builder()
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234567")
                .build();

        correntista = Correntista.builder()
                .id(1L)
                .nomeCompleto("Maria Silva")
                .endereco(endereco)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        request = CorrentistaRequest.builder()
                .nomeCompleto("Maria Silva")
                .endereco(endereco)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        atualizacaoRequest = CorrentistaAtualizacaoRequest.builder()
                .nomeCompleto("Maria Silva Santos")
                .build();

        resumoResponse = CorrentistaResumoResponse.builder()
                .id(1L)
                .nomeCompleto("Maria Silva")
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        response = CorrentistaResponse.builder()
                .id(1L)
                .nomeCompleto("Maria Silva")
                .endereco(endereco)
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();
    }

    @Test
    void listarTodos_deveRetornarListaDeCorrentistas() {
        when(correntistaRepository.findAll()).thenReturn(List.of(correntista));
        when(correntistaMapper.toResumoResponse(correntista)).thenReturn(resumoResponse);

        List<CorrentistaResumoResponse> resultado = correntistaService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maria Silva", resultado.get(0).getNomeCompleto());
        verify(correntistaRepository).findAll();
    }

    @Test
    void buscarPorIdentificador_deveRetornarCorrentista_QuandoEncontrado() {
        when(correntistaRepository.findByNumeroIdentificadorComContas("12345678900"))
                .thenReturn(Optional.of(correntista));
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.buscarPorIdentificador("12345678900");

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Maria Silva", resultado.getNomeCompleto());
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
        when(correntistaRepository.existsByTipoIdentificadorAndNumeroIdentificador(
                ETipoIdentificador.CPF, "12345678900")).thenReturn(false);
        when(correntistaMapper.toEntity(request)).thenReturn(correntista);
        when(correntistaRepository.save(any(Correntista.class))).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.cadastrar(request);

        assertNotNull(resultado);
        assertEquals("Maria Silva", resultado.getNomeCompleto());
        verify(correntistaRepository).save(any(Correntista.class));
    }

    @Test
    void cadastrar_deveLancarExcecao_QuandoIdentificadorDuplicado() {
        when(correntistaRepository.existsByTipoIdentificadorAndNumeroIdentificador(
                ETipoIdentificador.CPF, "12345678900")).thenReturn(true);

        IdentificadorDuplicadoException exception = assertThrows(
                IdentificadorDuplicadoException.class,
                () -> correntistaService.cadastrar(request));

        assertTrue(exception.getMessage().contains("CPF"));
    }

    @Test
    void atualizar_deveAtualizarCorrentista_QuandoDadosValidos() {
        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(correntistaRepository.save(correntista)).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.atualizar(1L, atualizacaoRequest);

        assertNotNull(resultado);
        assertEquals("Maria Silva Santos", correntista.getNomeCompleto());
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
        CorrentistaAtualizacaoRequest mesmoIdRequest = CorrentistaAtualizacaoRequest.builder()
                .tipoIdentificador(ETipoIdentificador.CPF)
                .numeroIdentificador("12345678900")
                .build();

        when(correntistaRepository.findById(1L)).thenReturn(Optional.of(correntista));
        when(correntistaRepository.save(correntista)).thenReturn(correntista);
        when(correntistaMapper.toResponse(correntista)).thenReturn(response);

        CorrentistaResponse resultado = correntistaService.atualizar(1L, mesmoIdRequest);

        assertNotNull(resultado);
        verify(correntistaRepository).save(correntista);
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
}
