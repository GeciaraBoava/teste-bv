package com.bv.geciara.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo do documento de identificação do correntista", enumAsRef = true)
public enum ETipoIdentificador {

    @Schema(description = "Cadastro de Pessoas Físicas")
    CPF,

    @Schema(description = "Cadastro Nacional da Pessoa Jurídica")
    CNPJ,

    @Schema(description = "Documento de viagem internacional")
    PASSAPORTE,

    @Schema(description = "Registro Geral")
    RG
}