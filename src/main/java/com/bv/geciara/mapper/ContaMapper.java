package com.bv.geciara.mapper;

import com.bv.geciara.dto.request.ContaRequest;
import com.bv.geciara.dto.response.ContaResponse;
import com.bv.geciara.model.entities.Conta;
import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.enums.EStatusConta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ContaMapper {

    public Conta toEntity(ContaRequest dto, Correntista correntista) {
        return Conta.builder()
                .numero(dto.numero())
                .agencia(dto.agencia())
                .codigoBanco(dto.codigoBanco())
                .tipo(dto.tipo())
                .saldo(BigDecimal.ZERO)
                .status(EStatusConta.ATIVA)
                .correntista(correntista)
                .build();
    }

    public ContaResponse toResponse(Conta entity) {
        return new ContaResponse(
                entity.getId(),
                entity.getNumero(),
                entity.getAgencia(),
                entity.getCodigoBanco(),
                entity.getTipo(),
                entity.getSaldo(),
                entity.getStatus(),
                entity.getCorrentista().getId(),
                entity.getDataCadastro(),
                entity.getDataAtualizacao()
        );
    }

}
