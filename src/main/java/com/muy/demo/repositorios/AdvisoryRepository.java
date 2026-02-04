package com.muy.demo.repositorios;

import com.muy.demo.models.Advisory;
import com.muy.demo.models.AdvisoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AdvisoryRepository extends JpaRepository<Advisory, Long> {

    List<Advisory> findByProgrammerId(Long programmerId);
    List<Advisory> findByExternalUserId(Long externalUserId);

    // Para evitar doble reserva (si hay asesoría confirmada o pendiente que se cruce con el horario)
    @Query("""
        select count(a) > 0
        from Advisory a
        where a.programmer.id = :programmerId
          and a.status in (:s1, :s2)
          and (a.startAt < :endAt and a.endAt > :startAt)
    """)
    boolean existsOverlap(Long programmerId,
                          LocalDateTime startAt,
                          LocalDateTime endAt,
                          AdvisoryStatus s1,
                          AdvisoryStatus s2);

    // Recordatorios: asesorías confirmadas que empiezan en una ventana
    List<Advisory> findByStatusAndStartAtBetween(AdvisoryStatus status, LocalDateTime from, LocalDateTime to);
}
