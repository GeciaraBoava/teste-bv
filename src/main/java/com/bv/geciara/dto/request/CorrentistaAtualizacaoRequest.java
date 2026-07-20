package com.bv.geciara.dto.request;

import com.bv.geciara.model.enums.ETipoIdentificador;
import com.bv.geciara.util.AtLeastOneNonNullField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@AtLeastOneNonNullField
@Schema(description = "Requisição para atualização de correntista. Todos os campos são opcionais — envie apenas o que deseja alterar.")
public record CorrentistaAtualizacaoRequest(

        @Schema(description = "Nome completo do correntista", example = "Maria Clara Fernandes da Silva")
        @Pattern(
                regexp = "^[\\p{L}\\s]+$",
                message = "Nome completo deve conter apenas letras e espaços"
        )
        @Size(max = 150, message = "Tamanho máximo de 150 caracteres excedido")
        String nomeCompleto,

        @Schema(description = "Endereço do correntista")
        EnderecoAtualizacaoRequest endereco,

        @Schema(description = "Tipo do documento de identificação", example = "CPF")
        ETipoIdentificador tipoIdentificador,

        @Schema(
                description = "Número do documento de identificação. "
                        + "Para CPF e RG: somente números (ex: '12345678909'). "
                        + "Para CNPJ: pode conter letras e números (ex: '12345678000190'). "
                        + "Para PASSAPORTE: alfanumérico (ex: 'AB1234567').",
                example = "12345678909")
        String numeroIdentificador

) {
}
