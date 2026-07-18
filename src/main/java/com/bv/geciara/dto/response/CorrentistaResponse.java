package com.bv.geciara.dto.response;

import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.model.enums.ETipoIdentificador;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta com os dados do correntista")
public class CorrentistaResponse {

    @Schema(description = "Identificador único do correntista", example = "1")
    private Long id;

    @Schema(description = "Nome completo do correntista", example = "Maria Clara Fernandes")
    private String nomeCompleto;

    @Schema(description = "Endereço do correntista")
    private Endereco endereco;

    @Schema(description = "Tipo do documento de identificação", example = "CPF")
    private ETipoIdentificador tipoIdentificador;

    @Schema(description = "Número do documento de identificação (somente números para CPF/RG)", example = "12345678909")
    private String numeroIdentificador;

    @Schema(description = "Data de cadastro do correntista")
    private LocalDateTime dataCadastro;

    @Schema(description = "Lista de contas vinculadas ao correntista")
    private List<ContaResponse> contas;

}