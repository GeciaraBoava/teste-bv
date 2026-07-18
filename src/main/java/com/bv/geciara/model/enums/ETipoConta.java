package com.bv.geciara.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo da conta bancária", enumAsRef = true)
public enum ETipoConta {

    @Schema(description = "Conta Corrente")
    CORRENTE,

    @Schema(description = "Conta Poupança")
    POUPANCA,

    @Schema(description = "Conta Salário")
    SALARIO
}
