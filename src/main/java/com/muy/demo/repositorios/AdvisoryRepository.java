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

    long countByProgrammerIdAndStatus(Long programmerId, AdvisoryStatus status);

    @Query("""
      select a.status, count(a)
      from Advisory a
      where a.programmer.id = :programmerId and a.startAt between :from and :to
      group by a.status
    """)
    List<Object[]> statusCountsForProgrammer(Long programmerId, LocalDateTime from, LocalDateTime to);
}
