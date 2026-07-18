package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.CorrentistaRequest;
import com.bv.geciara.dto.response.CorrentistaResumoResponse;
import com.bv.geciara.dto.response.CorrentistaResponse;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.util.SanitizacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CorrentistaMapper {

    private final ContaMapper contaMapper;

    public Correntista toEntity(CorrentistaRequest dto) {
        Endereco endereco = dto.getEndereco();
        if (endereco != null && endereco.getCep() != null) {
            endereco = Endereco.builder()
                    .logradouro(endereco.getLogradouro())
                    .numero(endereco.getNumero())
                    .bairro(endereco.getBairro())
                    .cidade(endereco.getCidade())
                    .estado(endereco.getEstado())
                    .cep(SanitizacaoUtil.sanitizarCep(endereco.getCep()))
                    .build();
        }

        return Correntista.builder()
                .nomeCompleto(dto.getNomeCompleto())
                .endereco(endereco)
                .tipoIdentificador(dto.getTipoIdentificador())
                .numeroIdentificador(SanitizacaoUtil.sanitizarDocumento(dto.getNumeroIdentificador()))
                .build();
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

}
