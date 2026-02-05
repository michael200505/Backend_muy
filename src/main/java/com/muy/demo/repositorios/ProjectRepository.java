package com.muy.demo.repositorios;

import com.muy.demo.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByProgrammerId(Long programmerId);

    // ✅ NUEVO: total de proyectos del programador
    long countByProgrammerId(Long programmerId);

    // ✅ YA EXISTE: total de proyectos activos del programador
    long countByProgrammerIdAndActiveTrue(Long programmerId);

    @Query("select p from Project p where p.programmer.email = :email")
    List<Project> findByProgrammerEmail(String email);
}
