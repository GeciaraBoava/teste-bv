package com.bv.geciara.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Endereço do correntista")
public class EnderecoAtualizacaoRequest {

    @Schema(description = "Logradouro", example = "Rua das Flores")
    @Size(max = 150)
    private String logradouro;

    @Schema(description = "Número", example = "123")
    @Size(max = 20)
    private String numero;

    @Schema(description = "Complemento", example = "Apartamento 101")
    @Size(max = 100)
    private String complemento;

    @Schema(description = "Bairro", example = "Centro")
    @Size(max = 100)
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    @Size(max = 100)
    private String cidade;

    @Schema(description = "UF", example = "SP")
    @Size(min = 2, max = 2)
    private String uf;

    @Schema(description = "CEP", example = "01001000")
    @Size(min = 8, max = 8)
    private String cep;
}