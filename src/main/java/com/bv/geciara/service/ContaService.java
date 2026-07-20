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
import com.bv.geciara.repository.ContaRepository;
import com.bv.geciara.repository.CorrentistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final CorrentistaRepository correntistaRepository;
    private final ContaMapper contaMapper;

    @Transactional(readOnly = true)
    public Page<ContaResponse> listarTodos(Pageable pageable) {
        return contaRepository.findAll(pageable)
                .map(contaMapper::toResponse);
    }

    @Transactional
    public ContaResponse cadastrar(ContaRequest request) {
        Correntista correntista = correntistaRepository.findById(request.correntistaId())
                .orElseThrow(() -> new CorrentistaNaoEncontradoException(request.correntistaId()));

        Conta conta = contaMapper.toEntity(request, correntista);

        if (conta.getStatus() == null) {
            conta.setStatus(EStatusConta.ATIVA);
        }

        correntista.adicionarConta(conta);
        Conta salva = contaRepository.save(conta);

        return contaMapper.toResponse(salva);
    }

    @Transactional
    public ContaResponse atualizar(Long id, ContaAtualizacaoRequest request) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        if (request.numero() != null) {
            conta.setNumero(request.numero());
        }

        if (request.agencia() != null) {
            conta.setAgencia(request.agencia());
        }

        if (request.codigoBanco() != null) {
            conta.setCodigoBanco(request.codigoBanco());
        }

        if (request.tipo() != null) {
            conta.setTipo(request.tipo());
        }

        if (request.saldo() != null) {
            conta.setSaldo(request.saldo());
        }

        Conta atualizada = contaRepository.save(conta);
        return contaMapper.toResponse(atualizada);
    }

    @Transactional
    public void encerrar(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        conta.setStatus(EStatusConta.ENCERRADA);
        contaRepository.save(conta);
    }

}
