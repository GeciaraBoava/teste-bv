package com.bv.geciara.model.entities;

import com.bv.geciara.model.EntidadeAuditavel;
import com.bv.geciara.model.enums.ETipoIdentificador;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
public class Correntista extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Embedded
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_identificador", nullable = false, length = 20)
    private ETipoIdentificador tipoIdentificador;

    @Column(name = "numero_identificador", nullable = false, length = 20)
    private String numeroIdentificador;

    @OneToMany(
            mappedBy = "correntista",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Conta> contas = new ArrayList<>();

    public void adicionarConta(Conta conta) {
        contas.add(conta);
        conta.setCorrentista(this);
    }

    public void removerConta(Conta conta) {
        contas.remove(conta);
        conta.setCorrentista(null);
    }

}