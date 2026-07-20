package com.bv.geciara.dto.response;

import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.model.enums.ETipoIdentificador;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta com os dados do correntista")
public record CorrentistaResponse(

        @Schema(description = "Identificador único do correntista", example = "1")
        Long id,

        @Schema(description = "Nome completo do correntista", example = "Maria Clara Fernandes")
        String nomeCompleto,

        @Schema(description = "Endereço do correntista")
        Endereco endereco,

        @Schema(description = "Tipo do documento de identificação", example = "CPF")
        ETipoIdentificador tipoIdentificador,

        @Schema(description = "Número do documento de identificação", example = "12345678909")
        String numeroIdentificador,

        @Schema(description = "Data de cadastro do correntista")
        LocalDateTime dataCadastro,

        @Schema(description = "Lista de contas vinculadas ao correntista")
        List<ContaResponse> contas

) {
}
