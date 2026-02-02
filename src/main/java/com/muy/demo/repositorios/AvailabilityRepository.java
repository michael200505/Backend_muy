package com.muy.demo.repositorios;

import com.muy.demo.models.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByProgrammerId(Long programmerId);
}
