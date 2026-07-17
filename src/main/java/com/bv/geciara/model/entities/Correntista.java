package com.bv.geciara.model.entities;

import com.bv.geciara.model.enums.ETipoIdentificador;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "correntista",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_correntista_identificador",
                        columnNames = {"tipo_identificador", "numero_identificador"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Correntista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_identificador", nullable = false, length = 20)
    private ETipoIdentificador tipoIdentificador;

    @Column(name = "numero_identificador", nullable = false, length = 20)
    private String numeroIdentificador;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "codigo_banco", nullable = false, length = 10)
    private String codigoBanco;

    @Column(nullable = false, length = 10)
    private String agencia;

    @Column(name = "numero_conta", nullable = false, length = 20)
    private String numeroConta;

}