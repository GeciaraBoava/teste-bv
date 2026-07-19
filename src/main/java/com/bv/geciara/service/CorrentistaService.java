package com.bv.geciara.service;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.exception.CorrentistaNaoEncontradoException;
import com.bv.geciara.exception.IdentificadorDuplicadoException;
import com.bv.geciara.exception.IdentificadorInvalidoException;
import com.bv.geciara.mapper.CorrentistaMapper;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.repository.CorrentistaRepository;
import com.bv.geciara.util.ValidacaoUtil;
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
        Correntista correntista = correntistaRepository.findByNumeroIdentificadorComContas(numeroIdentificador)
                .orElseThrow(() -> new CorrentistaNaoEncontradoException(numeroIdentificador));
        return correntistaMapper.toResponse(correntista);
    }

    @Transactional
    public CorrentistaResponse cadastrar(CorrentistaRequest request) {
        if (!ValidacaoUtil.isIdentificadorValid(
                        request.getTipoIdentificador(),
                        request.getNumeroIdentificador())) {
            throw new IdentificadorInvalidoException(
                    request.getTipoIdentificador().name(),
                    request.getNumeroIdentificador()
            );
        }

        if (correntistaRepository.existsByNumeroIdentificador(request.getNumeroIdentificador())) {
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

        String numeroIdentificadorAtualizado = request.getNumeroIdentificador() != null
                ? request.getNumeroIdentificador()
                : null;

        if (request.getTipoIdentificador() != null && numeroIdentificadorAtualizado != null) {

            boolean mesmoIdentificador =
                    correntista.getTipoIdentificador() == request.getTipoIdentificador()
                        && correntista.getNumeroIdentificador().equals(numeroIdentificadorAtualizado);

            if (!mesmoIdentificador
                    && correntistaRepository.existsByNumeroIdentificador(numeroIdentificadorAtualizado)) {
                throw new IdentificadorDuplicadoException(
                        request.getTipoIdentificador().name(),
                        request.getNumeroIdentificador()
                );
            }
        }

        correntistaMapper.updateEntity(request, correntista);

        return correntistaMapper.toResponse(correntistaRepository.save(correntista));
    }

    @Transactional
    public void excluir(Long id) {
        if (!correntistaRepository.existsById(id)) {
            throw new CorrentistaNaoEncontradoException(id);
        }
        correntistaRepository.deleteById(id);
    }
}
