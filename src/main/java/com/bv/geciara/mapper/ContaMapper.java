package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.model.entities.Conta;
import com.bv.geciara.model.entities.Correntista;
import org.springframework.stereotype.Component;

@Component
public class ContaMapper {

    public Conta toEntity(ContaRequest dto, Correntista correntista) {
        return Conta.builder()
                .numero(dto.getNumero())
                .agencia(dto.getAgencia())
                .tipo(dto.getTipo())
                .saldo(dto.getSaldo())
                .status(dto.getStatus())
                .correntista(correntista)
                .build();
    }

    public ContaResponse toResponse(Conta entity) {
        return ContaResponse.builder()
                .id(entity.getId())
                .numero(entity.getNumero())
                .agencia(entity.getAgencia())
                .tipo(entity.getTipo())
                .saldo(entity.getSaldo())
                .status(entity.getStatus())
                .correntistaId(entity.getCorrentista().getId())
                .dataCadastro(entity.getDataCadastro())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }

}
