package com.bv.geciara.dto.request;

import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criação de conta vinculada a um correntista")
public class ContaRequest {

    @Schema(description = "ID do correntista ao qual a conta será vinculada", example = "1")
    @NotNull(message = "ID do correntista é obrigatório")
    private Long correntistaId;

    @Schema(description = "Número da conta (6 dígitos)", example = "456789")
    @NotNull(message = "Número da conta é obrigatório")
    @Size(min = 1, max = 20, message = "Tamanho máximo de 20 caracteres excedido")
    private String numero;

    @Schema(description = "Número da agência (4 dígitos)", example = "1234")
    @NotNull(message = "Agência é obrigatória")
    private Integer agencia;

    @Schema(description = "Tipo da conta", example = "CORRENTE")
    @NotNull(message = "Tipo da conta é obrigatório")
    private ETipoConta tipo;

}
