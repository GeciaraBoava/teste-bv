package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.CorrentistaAtualizacaoRequest;
import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.request.EnderecoAtualizacaoRequest;
import com.bv.geciara.dto.request.EnderecoRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.dto.response.ContaResponse;
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
        Endereco endereco = null;

        if (dto.endereco() != null) {
            EnderecoRequest er = dto.endereco();
            endereco = Endereco.builder()
                    .logradouro(er.logradouro())
                    .numero(er.numero())
                    .complemento(er.complemento())
                    .bairro(er.bairro())
                    .cidade(er.cidade())
                    .uf(er.uf())
                    .cep(er.cep())
                    .build();
        }

        return Correntista.builder()
                .nomeCompleto(dto.nomeCompleto())
                .endereco(endereco)
                .tipoIdentificador(dto.tipoIdentificador())
                .numeroIdentificador(dto.numeroIdentificador())
                .build();
    }

    public CorrentistaResumoResponse toResumoResponse(Correntista entity) {
        return new CorrentistaResumoResponse(
                entity.getId(),
                entity.getNomeCompleto(),
                entity.getTipoIdentificador(),
                entity.getNumeroIdentificador()
        );
    }

    public CorrentistaResponse toResponse(Correntista entity) {
        List<ContaResponse> contasResponse = entity.getContas() != null
                ? entity.getContas().stream()
                    .map(contaMapper::toResponse)
                    .toList()
                : List.of();

        return new CorrentistaResponse(
                entity.getId(),
                entity.getNomeCompleto(),
                entity.getEndereco(),
                entity.getTipoIdentificador(),
                entity.getNumeroIdentificador(),
                entity.getDataCadastro(),
                contasResponse
        );
    }

    public void updateEntity(CorrentistaAtualizacaoRequest dto, Correntista entity) {
        if (dto.nomeCompleto() != null) {
            entity.setNomeCompleto(dto.nomeCompleto());
        }

        if (dto.endereco() != null) {
            EnderecoAtualizacaoRequest er = dto.endereco();
            Endereco endereco = entity.getEndereco();

            if (endereco == null) {
                endereco = new Endereco();
                entity.setEndereco(endereco);
            }

            if (er.logradouro() != null) endereco.setLogradouro(er.logradouro());
            if (er.numero() != null) endereco.setNumero(er.numero());
            if (er.complemento() != null) endereco.setComplemento(er.complemento());
            if (er.bairro() != null) endereco.setBairro(er.bairro());
            if (er.cidade() != null) endereco.setCidade(er.cidade());
            if (er.uf() != null) endereco.setUf(er.uf());
            if (er.cep() != null) endereco.setCep(er.cep());
        }

        if (dto.tipoIdentificador() != null) {
            entity.setTipoIdentificador(dto.tipoIdentificador());

            if (dto.numeroIdentificador() != null
                    && ValidacaoUtil.isIdentificadorValid(dto.tipoIdentificador(), dto.numeroIdentificador())) {
                entity.setNumeroIdentificador(dto.numeroIdentificador());
            } else {
                throw new IdentificadorInvalidoException(
                        dto.tipoIdentificador().name(),
                        dto.numeroIdentificador()
                );
            }
        }
    }

}
