package com.bv.geciara.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Endereço do correntista")
public record EnderecoRequest(

        @Schema(description = "Logradouro", example = "Rua das Flores")
        @Pattern(
                regexp = "^[\\p{L}\\s]+$",
                message = "Logradouro deve conter apenas letras e espaços"
        )
        @NotBlank(message = "Logradouro é obrigatório")
        @Size(max = 150, message = "Tamanho máximo de 150 caracteres excedido")
        String logradouro,

        @Schema(description = "Número", example = "123")
        @NotBlank(message = "Número é obrigatório")
        @Pattern(
                regexp = "^[0-9]+$",
                message = "O campo deve conter apenas números"
        )
        @Size(max = 20, message = "Tamanho máximo de 20 caracteres excedido")
        String numero,

        @Schema(description = "Complemento", example = "Apartamento 101")
        @Size(max = 100, message = "Tamanho máximo de 100 caracteres excedido")
        String complemento,

        @Schema(description = "Bairro", example = "Centro")
        @Pattern(
                regexp = "^[\\p{L}\\s]+$",
                message = "Bairro deve conter apenas letras e espaços"
        )
        @NotBlank(message = "Bairro é obrigatório")
        @Size(max = 100, message = "Tamanho máximo de 100 caracteres excedido")
        String bairro,

        @Schema(description = "Cidade", example = "São Paulo")
        @Pattern(
                regexp = "^[\\p{L}\\s]+$",
                message = "Cidade deve conter apenas letras e espaços"
        )
        @NotBlank(message = "Cidade é obrigatória")
        @Size(max = 100, message = "Tamanho máximo de 100 caracteres excedido")
        String cidade,

        @Schema(description = "UF", example = "SP")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ\\s]+$",
                message = "UF deve conter apenas letras"
        )
        @NotBlank(message = "UF é obrigatória")
        @Size(min = 2, max = 2, message = "Tamanho deve ser de 2 caracteres")
        String uf,

        @Schema(description = "CEP", example = "01001000")
        @Pattern(
                regexp = "^[0-9]+$",
                message = "O campo deve conter apenas números"
        )
        @NotBlank(message = "CEP é obrigatório")
        @Size(min = 8, max = 8, message = "Tamanho deve ser de 8 caracteres")
        String cep

) {
}
