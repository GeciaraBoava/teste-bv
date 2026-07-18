package com.bv.geciara.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status atual da conta bancária", enumAsRef = true)
public enum EStatusConta {

    @Schema(description = "Conta ativa e operacional")
    ATIVA,

    @Schema(description = "Conta temporariamente bloqueada")
    BLOQUEADA,

    @Schema(description = "Conta encerrada definitivamente")
    ENCERRADA
}
