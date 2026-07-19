package com.bv.geciara.dto.request;

import com.bv.geciara.model.entities.Endereco;
import com.bv.geciara.model.enums.ETipoIdentificador;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para criação de correntista")
public class CorrentistaRequest {

    @Schema(description = "Nome completo do correntista", example = "Maria Clara Fernandes")
    @NotBlank(message = "Nome completo é obrigatório")
    @Pattern(
            regexp = "^[\\p{L}\\s]+$",
            message = "Nome completo deve conter apenas letras e espaços"
    )
    @Size(max = 150)
    private String nomeCompleto;

    @Schema(description = "Endereço do correntista")
    @Valid
    @NotNull(message = "Endereço é obrigatório")
    private EnderecoRequest endereco;

    @Schema(description = "Tipo do documento de identificação", example = "CPF")
    @NotNull(message = "Tipo do identificador é obrigatório")
    private ETipoIdentificador tipoIdentificador;

    @Schema(
            description = "Número do documento de identificação. "
                    + "Para CPF e RG: somente números (ex: '12345678909'). "
                    + "Para CNPJ: pode conter letras e números (ex: '12345678000190'). "
                    + "Para PASSAPORTE: alfanumérico (ex: 'AB1234567'). "
                    + "Pontos, traços e barras serão removidos automaticamente.",
            example = "12345678909")
    @NotBlank(message = "Número do identificador é obrigatório")
    @Size(max = 20)
    private String numeroIdentificador;

}