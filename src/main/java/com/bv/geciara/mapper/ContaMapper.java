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
                .numero(dto.getNumero())
                .agencia(dto.getAgencia())
                .codigoBanco(dto.getCodigoBanco())
                .tipo(dto.getTipo())
                .saldo(BigDecimal.ZERO)
                .status(EStatusConta.ATIVA)
                .correntista(correntista)
                .build();
    }

    public ContaResponse toResponse(Conta entity) {
        return ContaResponse.builder()
                .id(entity.getId())
                .numero(entity.getNumero())
                .agencia(entity.getAgencia())
                .codigoBanco(entity.getCodigoBanco())
                .tipo(entity.getTipo())
                .saldo(entity.getSaldo())
                .status(entity.getStatus())
                .correntistaId(entity.getCorrentista().getId())
                .dataCadastro(entity.getDataCadastro())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }

}
