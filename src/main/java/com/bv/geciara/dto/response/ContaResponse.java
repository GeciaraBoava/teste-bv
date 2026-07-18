package com.bv.geciara.dto.response;

import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com dados da conta bancária")
public class ContaResponse {

    @Schema(description = "Identificador único da conta", example = "1")
    private Long id;

    @Schema(description = "Número da conta", example = "456789")
    private String numero;

    @Schema(description = "Número da agência", example = "1234")
    private Integer agencia;

    @Schema(description = "Tipo da conta", example = "CORRENTE")
    private ETipoConta tipo;

    @Schema(description = "Saldo disponível na conta. Número com 2 casas decimais", example = "5000.00")
    private BigDecimal saldo;

    @Schema(description = "Status da conta", example = "ATIVA")
    private EStatusConta status;

    @Schema(description = "ID do correntista vinculado", example = "1")
    private Long correntistaId;

    @Schema(description = "Data de criação da conta")
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização")
    private LocalDateTime dataAtualizacao;

}
