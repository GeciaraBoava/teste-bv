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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CorrentistaService {

    private final CorrentistaRepository correntistaRepository;
    private final CorrentistaMapper correntistaMapper;

    @Transactional(readOnly = true)
    public Page<CorrentistaResumoResponse> listarTodos(Pageable pageable) {
        return correntistaRepository.findAll(pageable)
                .map(correntistaMapper::toResumoResponse);
    }

    @Transactional(readOnly = true)
    public Page<CorrentistaResponse> listarTodosCompletos(Pageable pageable) {
        return correntistaRepository.findAllComContas(pageable)
                .map(correntistaMapper::toResponse);
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
                        request.tipoIdentificador(),
                        request.numeroIdentificador())) {
            throw new IdentificadorInvalidoException(
                    request.tipoIdentificador().name(),
                    request.numeroIdentificador()
            );
        }

        if (correntistaRepository.existsByNumeroIdentificador(request.numeroIdentificador())) {
            throw new IdentificadorDuplicadoException(
                    request.tipoIdentificador().name(),
                    request.numeroIdentificador()
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

        String numeroIdentificadorAtualizado = request.numeroIdentificador() != null
                ? request.numeroIdentificador()
                : null;

        if (request.tipoIdentificador() != null && numeroIdentificadorAtualizado != null) {

            boolean mesmoIdentificador =
                    correntista.getTipoIdentificador() == request.tipoIdentificador()
                        && correntista.getNumeroIdentificador().equals(numeroIdentificadorAtualizado);

            if (!mesmoIdentificador
                    && correntistaRepository.existsByNumeroIdentificador(numeroIdentificadorAtualizado)) {
                throw new IdentificadorDuplicadoException(
                        request.tipoIdentificador().name(),
                        request.numeroIdentificador()
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
