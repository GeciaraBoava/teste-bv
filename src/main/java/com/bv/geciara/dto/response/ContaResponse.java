package com.bv.geciara.dto.response;

import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Resposta com dados da conta bancária")
public record ContaResponse(

        @Schema(description = "Identificador único da conta", example = "1")
        Long id,

        @Schema(description = "Número da conta", example = "456789")
        String numero,

        @Schema(description = "Número da agência", example = "1234")
        Integer agencia,

        @Schema(description = "Código do banco", example = "001")
        String codigoBanco,

        @Schema(description = "Tipo da conta", example = "CORRENTE")
        ETipoConta tipo,

        @Schema(description = "Saldo disponível na conta", example = "5000.00")
        BigDecimal saldo,

        @Schema(description = "Status da conta", example = "ATIVA")
        EStatusConta status,

        @Schema(description = "ID do correntista vinculado", example = "1")
        Long correntistaId,

        @Schema(description = "Data de criação da conta")
        LocalDateTime dataCadastro,

        @Schema(description = "Data da última atualização")
        LocalDateTime dataAtualizacao

) {
}
