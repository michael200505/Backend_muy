package com.muy.demo.repositorios;

import com.muy.demo.models.Advisory;
import com.muy.demo.models.AdvisoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdvisoryRepository extends JpaRepository<Advisory, Long> {

    List<Advisory> findByProgrammerId(Long programmerId);
    List<Advisory> findByExternalUserId(Long externalUserId);

    // ✅ NUEVO: conteo por estado para un programador
    long countByProgrammerIdAndStatus(Long programmerId, AdvisoryStatus status);

    // ✅ NUEVO: próximas asesorías CONFIRMED a partir de "now"
    @Query("""
       select a
       from Advisory a
       where a.programmer.id = :programmerId
         and a.status = com.muy.demo.models.AdvisoryStatus.CONFIRMED
         and a.startAt >= :now
       order by a.startAt asc
    """)
    List<Advisory> findUpcomingConfirmed(@Param("programmerId") Long programmerId,
                                         @Param("now") LocalDateTime now);

    // ✅ NUEVO: conteo por mes (usa date_format de MySQL)
    @Query("""
       select function('date_format', a.startAt, '%Y-%m') as ym, count(a)
       from Advisory a
       where a.programmer.id = :programmerId
         and a.startAt >= :from
       group by function('date_format', a.startAt, '%Y-%m')
       order by ym asc
    """)
    List<Object[]> countByMonth(@Param("programmerId") Long programmerId,
                                @Param("from") LocalDateTime from);

    // Para evitar doble reserva (si hay asesoría confirmada o pendiente que se cruce con el horario)
    @Query("""
        select count(a) > 0
        from Advisory a
        where a.programmer.id = :programmerId
          and a.status in (:s1, :s2)
          and (a.startAt < :endAt and a.endAt > :startAt)
    """)
    boolean existsOverlap(@Param("programmerId") Long programmerId,
                          @Param("startAt") LocalDateTime startAt,
                          @Param("endAt") LocalDateTime endAt,
                          @Param("s1") AdvisoryStatus s1,
                          @Param("s2") AdvisoryStatus s2);

    // Recordatorios: asesorías confirmadas que empiezan en una ventana
    List<Advisory> findByStatusAndStartAtBetween(AdvisoryStatus status, LocalDateTime from, LocalDateTime to);

    // =========================
    // ✅ MÁS SEGURO: métodos por email
    // =========================

    @Query("""
      select a from Advisory a
      where a.id = :id and a.programmer.email = :email
    """)
    Optional<Advisory> findByIdAndProgrammerEmail(@Param("id") Long id,
                                                  @Param("email") String email);

    @Query("""
      select a from Advisory a
      where a.id = :id and a.externalUser.email = :email
    """)
    Optional<Advisory> findByIdAndExternalEmail(@Param("id") Long id,
                                                @Param("email") String email);

    @Query("""
      select a from Advisory a
      where a.programmer.email = :email
      order by a.startAt desc
    """)
    List<Advisory> findByProgrammerEmail(@Param("email") String email);

    @Query("""
      select a from Advisory a
      where a.externalUser.email = :email
      order by a.startAt desc
    """)
    List<Advisory> findByExternalEmail(@Param("email") String email);
}
