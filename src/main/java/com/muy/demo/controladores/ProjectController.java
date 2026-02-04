package com.muy.demo.controladores;

import com.muy.demo.modelosdto.CreateProjectRequest;
import com.muy.demo.modelosdto.UpdateProjectRequest;
import com.muy.demo.models.Project;
import com.muy.demo.servicios.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('PROGRAMMER')")
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Project> create(@Valid @RequestBody CreateProjectRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/programmer/{programmerId}")
    public ResponseEntity<List<Project>> byProgrammer(@PathVariable Long programmerId) {
        return ResponseEntity.ok(service.listByProgrammer(programmerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Long id, @RequestBody UpdateProjectRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
