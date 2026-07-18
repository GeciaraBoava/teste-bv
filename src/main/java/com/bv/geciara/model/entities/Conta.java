package com.bv.geciara.model.entities;

import com.bv.geciara.model.EntidadeAuditavel;
import com.bv.geciara.model.enums.EStatusConta;
import com.bv.geciara.model.enums.ETipoConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conta")
public class Conta extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private Integer agencia;

    @Enumerated(EnumType.STRING)
    private ETipoConta tipo;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    private EStatusConta status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correntista_id", nullable = false)
    private Correntista correntista;

}
