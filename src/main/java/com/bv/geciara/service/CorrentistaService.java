package com.bv.geciara.service;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.exception.CorrentistaNaoEncontradoException;
import com.bv.geciara.exception.IdentificadorDuplicadoException;
import com.bv.geciara.mapper.CorrentistaMapper;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.repository.CorrentistaRepository;
import com.bv.geciara.util.SanitizacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorrentistaService {

    private final CorrentistaRepository correntistaRepository;
    private final CorrentistaMapper correntistaMapper;

    @Transactional(readOnly = true)
    public List<CorrentistaResumoResponse> listarTodos() {
        return correntistaRepository.findAll()
                .stream()
                .map(correntistaMapper::toResumoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CorrentistaResponse> listarTodosCompletos() {
        return correntistaRepository.findAllComContas()
                .stream()
                .map(correntistaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CorrentistaResponse buscarPorIdentificador(String numeroIdentificador) {
        String sanitizado = SanitizacaoUtil.sanitizarDocumento(numeroIdentificador);
        Correntista correntista = correntistaRepository.findByNumeroIdentificadorComContas(sanitizado)
                .orElseThrow(() -> new CorrentistaNaoEncontradoException(sanitizado));
        return correntistaMapper.toResponse(correntista);
    }

    @Transactional
    public CorrentistaResponse cadastrar(CorrentistaRequest request) {
        String numeroSanitizado = SanitizacaoUtil.sanitizarDocumento(request.getNumeroIdentificador());

        if (correntistaRepository.existsByTipoIdentificadorAndNumeroIdentificador(
                request.getTipoIdentificador(), numeroSanitizado)) {
            throw new IdentificadorDuplicadoException(
                    request.getTipoIdentificador().name(),
                    request.getNumeroIdentificador()
            );
        }

        Correntista correntista = correntistaMapper.toEntity(request);
        Correntista salvo = correntistaRepository.save(correntista);
        return correntistaMapper.toResponse(salvo);
    }

    @Transactional
    public CorrentistaResponse atualizar(Long id, CorrentistaAtualizacaoRequest request) {
        Correntista correntista = correntistaRepository.findById(id)
                .orElseThrow(() -> new CorrentistaNaoEncontradoException(id));

        if (request.getNomeCompleto() != null) {
            correntista.setNomeCompleto(request.getNomeCompleto());
        }

        if (request.getEndereco() != null) {
            if (request.getEndereco().getCep() != null) {
                request.getEndereco().setCep(SanitizacaoUtil.sanitizarCep(request.getEndereco().getCep()));
            }
            correntista.setEndereco(request.getEndereco());
        }

        if (request.getTipoIdentificador() != null && request.getNumeroIdentificador() != null) {
            String numeroSanitizado = SanitizacaoUtil.sanitizarDocumento(request.getNumeroIdentificador());

            boolean mesmoIdentificador = correntista.getTipoIdentificador() == request.getTipoIdentificador()
                    && correntista.getNumeroIdentificador().equals(numeroSanitizado);

            if (!mesmoIdentificador) {
                if (correntistaRepository.existsByTipoIdentificadorAndNumeroIdentificador(
                        request.getTipoIdentificador(), numeroSanitizado)) {
                    throw new IdentificadorDuplicadoException(
                            request.getTipoIdentificador().name(),
                            request.getNumeroIdentificador()
                    );
                }
            }

            correntista.setTipoIdentificador(request.getTipoIdentificador());
            correntista.setNumeroIdentificador(numeroSanitizado);
        }

        Correntista atualizado = correntistaRepository.save(correntista);
        return correntistaMapper.toResponse(atualizado);
    }

    @Transactional
    public void excluir(Long id) {
        if (!correntistaRepository.existsById(id)) {
            throw new CorrentistaNaoEncontradoException(id);
        }
        correntistaRepository.deleteById(id);
    }
}
