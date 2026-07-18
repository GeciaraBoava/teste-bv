package com.bv.geciara.dto.response;

import com.bv.geciara.model.enums.ETipoIdentificador;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resumo dos dados do correntista")
public class CorrentistaResumoResponse {

    @Schema(description = "Identificador único do correntista", example = "1")
    private Long id;

    @Schema(description = "Nome completo do correntista", example = "Maria Clara Fernandes")
    private String nomeCompleto;

    @Schema(description = "Tipo do documento de identificação", example = "CPF")
    private ETipoIdentificador tipoIdentificador;

    @Schema(description = "Número do documento de identificação (somente números para CPF/RG)", example = "12345678909")
    private String numeroIdentificador;

}
