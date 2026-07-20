package com.bv.geciara.repository;

import com.bv.geciara.model.entities.Correntista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrentistaRepository extends JpaRepository<Correntista, Long> {

    boolean existsByNumeroIdentificador(
           String numeroIdentificador
    );

    @Query("SELECT c FROM Correntista c LEFT JOIN FETCH c.contas WHERE c.numeroIdentificador = :numeroIdentificador")
    Optional<Correntista> findByNumeroIdentificadorComContas(@Param("numeroIdentificador") String numeroIdentificador);

    @Query(value = "SELECT DISTINCT c FROM Correntista c LEFT JOIN FETCH c.contas",
            countQuery = "SELECT COUNT(DISTINCT c) FROM Correntista c")
    Page<Correntista> findAllComContas(Pageable pageable);

}
