package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.request.EnderecoAtualizacaoRequest;
import com.bv.geciara.dto.request.EnderecoRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.exception.IdentificadorDuplicadoException;
import com.bv.geciara.exception.IdentificadorInvalidoException;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.util.ValidacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CorrentistaMapper {

    private final ContaMapper contaMapper;

    public Correntista toEntity(CorrentistaRequest dto) {

        EnderecoRequest enderecoRequest = dto.getEndereco();

        if (enderecoRequest != null) {
            Endereco endereco = Endereco.builder()
                    .logradouro(enderecoRequest.getLogradouro())
                    .numero(enderecoRequest.getNumero())
                    .bairro(enderecoRequest.getBairro())
                    .cidade(enderecoRequest.getCidade())
                    .uf(enderecoRequest.getUf())
                    .cep(enderecoRequest.getCep())
                    .build();

            return Correntista.builder()
                    .nomeCompleto(dto.getNomeCompleto())
                    .endereco(endereco)
                    .tipoIdentificador(dto.getTipoIdentificador())
                    .numeroIdentificador(dto.getNumeroIdentificador())
                    .build();
        }
        return null;
    }

    public CorrentistaResumoResponse toResumoResponse(Correntista entity) {

        return CorrentistaResumoResponse.builder()
                .id(entity.getId())
                .nomeCompleto(entity.getNomeCompleto())
                .tipoIdentificador(entity.getTipoIdentificador())
                .numeroIdentificador(entity.getNumeroIdentificador())
                .build();
    }

    public CorrentistaResponse toResponse(Correntista entity) {
        List<ContaResponse> contasResponse = entity.getContas() != null
                ? entity.getContas().stream()
                    .map(contaMapper::toResponse)
                    .toList()
                : List.of();

        return CorrentistaResponse.builder()
                .id(entity.getId())
                .nomeCompleto(entity.getNomeCompleto())
                .endereco(entity.getEndereco())
                .tipoIdentificador(entity.getTipoIdentificador())
                .numeroIdentificador(entity.getNumeroIdentificador())
                .dataCadastro(entity.getDataCadastro())
                .contas(contasResponse)
                .build();
    }

    public void updateEntity(CorrentistaAtualizacaoRequest dto, Correntista entity) {

        if (dto.getNomeCompleto() != null) {
            entity.setNomeCompleto(dto.getNomeCompleto());
        }

        if (dto.getEndereco() != null) {
            EnderecoAtualizacaoRequest enderecoRequest = dto.getEndereco();

            Endereco endereco = entity.getEndereco();

            if (endereco == null) {
                endereco = new Endereco();
                entity.setEndereco(endereco);
            }

            if (enderecoRequest.getLogradouro() != null) {
                endereco.setLogradouro(enderecoRequest.getLogradouro());
            }

            if (enderecoRequest.getNumero() != null) {
                endereco.setNumero(enderecoRequest.getNumero());
            }

            if (enderecoRequest.getComplemento() != null) {
                endereco.setComplemento(enderecoRequest.getComplemento());
            }

            if (enderecoRequest.getBairro() != null) {
                endereco.setBairro(enderecoRequest.getBairro());
            }

            if (enderecoRequest.getCidade() != null) {
                endereco.setCidade(enderecoRequest.getCidade());
            }

            if (enderecoRequest.getUf() != null) {
                endereco.setUf(enderecoRequest.getUf());
            }

            if (enderecoRequest.getCep() != null) {
                endereco.setCep(enderecoRequest.getCep());
            }
        }

        if (dto.getTipoIdentificador() != null) {
            entity.setTipoIdentificador(dto.getTipoIdentificador());

            if (dto.getNumeroIdentificador() != null
                    && ValidacaoUtil.isIdentificadorValid(dto.getTipoIdentificador(), dto.getNumeroIdentificador())) {
                entity.setNumeroIdentificador(dto.getNumeroIdentificador());
            } else {
                throw new IdentificadorInvalidoException(
                        dto.getTipoIdentificador().name(),
                        dto.getNumeroIdentificador()
                );
            }
        }

    }

}
