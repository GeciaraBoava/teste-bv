package com.bv.geciara.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Endereço do correntista para atualização")
public record EnderecoAtualizacaoRequest(

        @Schema(description = "Logradouro", example = "Rua das Flores")
        @Size(max = 150, message = "Tamanho máximo de 150 caracteres excedido")
        String logradouro,

        @Schema(description = "Número", example = "123")
        @Size(max = 20, message = "Tamanho máximo de 20 caracteres excedido")
        String numero,

        @Schema(description = "Complemento", example = "Apartamento 101")
        @Size(max = 100, message = "Tamanho máximo de 100 caracteres excedido")
        String complemento,

        @Schema(description = "Bairro", example = "Centro")
        @Size(max = 100, message = "Tamanho máximo de 100 caracteres excedido")
        String bairro,

        @Schema(description = "Cidade", example = "São Paulo")
        @Size(max = 100, message = "Tamanho máximo de 100 caracteres excedido")
        String cidade,

        @Schema(description = "UF", example = "SP")
        @Size(min = 2, max = 2, message = "Tamanho deve ser de 2 caracteres")
        String uf,

        @Schema(description = "CEP", example = "01001000")
        @Size(min = 8, max = 8, message = "Tamanho deve ser de 8 caracteres")
        String cep

) {
}
