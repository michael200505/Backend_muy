package com.muy.demo.controladores;

import com.muy.demo.modelosdto.CreateProjectRequest;
import com.muy.demo.modelosdto.UpdateProjectRequest;
import com.muy.demo.models.Project;
import com.muy.demo.seguridad.AuthUtil;
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
        // ignoramos programmerId del body (si viene)
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.createForProgrammer(email, req));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Project>> myProjects() {
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.listByProgrammerEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Long id, @RequestBody UpdateProjectRequest req) {
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.updateOwned(email, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        String email = AuthUtil.currentEmail();
        service.deleteOwned(email, id);
        return ResponseEntity.ok().build();
    }
}
