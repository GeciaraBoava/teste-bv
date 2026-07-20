package com.bv.geciara.dto.response;

import com.bv.geciara.model.enums.ETipoIdentificador;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo dos dados do correntista")
public record CorrentistaResumoResponse(

        @Schema(description = "Identificador único do correntista", example = "1")
        Long id,

        @Schema(description = "Nome completo do correntista", example = "Maria Clara Fernandes")
        String nomeCompleto,

        @Schema(description = "Tipo do documento de identificação", example = "CPF")
        ETipoIdentificador tipoIdentificador,

        @Schema(description = "Número do documento de identificação", example = "12345678909")
        String numeroIdentificador

) {
}
