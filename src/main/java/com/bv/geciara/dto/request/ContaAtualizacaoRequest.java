package com.bv.geciara.dto.request;

import com.bv.geciara.model.enums.ETipoConta;
import com.bv.geciara.util.AtLeastOneNonNullField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@AtLeastOneNonNullField
@Schema(description = "Requisição para atualização de conta bancária. Todos os campos são opcionais — envie apenas o que deseja alterar.")
public record ContaAtualizacaoRequest(

        @Schema(description = "Número da conta", example = "456789")
        @Size(min = 1, max = 20, message = "Tamanho máximo de 20 caracteres excedido")
        String numero,

        @Schema(description = "Número da agência", example = "1234")
        Integer agencia,

        @Schema(description = "Código do banco (3 dígitos)", example = "001")
        @Size(min = 3, max = 3, message = "Código do banco deve ter exatamente 3 caracteres")
        String codigoBanco,

        @Schema(description = "Tipo da conta", example = "CORRENTE")
        ETipoConta tipo,

        @Schema(description = "Saldo da conta", example = "5000.00")
        BigDecimal saldo

) {
}
