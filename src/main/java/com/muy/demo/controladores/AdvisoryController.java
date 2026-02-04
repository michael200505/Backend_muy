package com.muy.demo.controladores;

import com.muy.demo.modelosdto.CreateAdvisoryRequest;
import com.muy.demo.modelosdto.UpdateAdvisoryStatusRequest;
import com.muy.demo.models.Advisory;
import com.muy.demo.servicios.AdvisoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advisories")
@CrossOrigin(origins = "*")
public class AdvisoryController {

    private final AdvisoryService service;

    public AdvisoryController(AdvisoryService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('EXTERNAL')")
    public ResponseEntity<Advisory> create(@Valid @RequestBody CreateAdvisoryRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PROGRAMMER')")
    public ResponseEntity<Advisory> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateAdvisoryStatusRequest req) {
        return ResponseEntity.ok(service.updateStatus(id, req));
    }

    @GetMapping("/programmer/{programmerId}")
    @PreAuthorize("hasRole('PROGRAMMER')")
    public ResponseEntity<List<Advisory>> byProgrammer(@PathVariable Long programmerId) {
        return ResponseEntity.ok(service.listByProgrammer(programmerId));
    }

    @GetMapping("/external/{externalUserId}")
    @PreAuthorize("hasRole('EXTERNAL')")
    public ResponseEntity<List<Advisory>> byExternal(@PathVariable Long externalUserId) {
        return ResponseEntity.ok(service.listByExternal(externalUserId));
    }
}
