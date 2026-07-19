package com.bv.geciara.repository;

import com.bv.geciara.model.entities.Correntista;
import com.bv.geciara.model.enums.ETipoIdentificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorrentistaRepository extends JpaRepository<Correntista, Long> {

    boolean existsByNumeroIdentificador(
           String numeroIdentificador
    );

    @Query("SELECT c FROM Correntista c LEFT JOIN FETCH c.contas WHERE c.numeroIdentificador = :numeroIdentificador")
    Optional<Correntista> findByNumeroIdentificadorComContas(@Param("numeroIdentificador") String numeroIdentificador);

    @Query("SELECT DISTINCT c FROM Correntista c LEFT JOIN FETCH c.contas")
    List<Correntista> findAllComContas();

}
