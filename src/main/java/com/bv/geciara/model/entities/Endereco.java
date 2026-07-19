package com.bv.geciara.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Endereco {

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    @Column(name = "endereco_logradouro")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    @Column(name = "endereco_numero")
    private String numero;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "endereco_bairro")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    @Column(name = "endereco_complemento")
    private String complemento;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "endereco_cidade")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "UF deve ter exatamente 2 caracteres")
    @Column(name = "endereco_uf")
    private String uf;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "[\\d.-]+", message = "CEP deve conter 8 dígitos numéricos")
    @Column(name = "endereco_cep")
    private String cep;
}
