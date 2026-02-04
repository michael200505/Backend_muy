package com.muy.demo.controladores;

import com.muy.demo.modelosdto.CreateAvailabilityRequest;
import com.muy.demo.models.Availability;
import com.muy.demo.servicios.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('PROGRAMMER')")
@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Availability> create(@Valid @RequestBody CreateAvailabilityRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/programmer/{programmerId}")
    public ResponseEntity<List<Availability>> byProgrammer(@PathVariable Long programmerId) {
        return ResponseEntity.ok(service.listByProgrammer(programmerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
