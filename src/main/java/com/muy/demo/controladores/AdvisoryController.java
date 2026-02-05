package com.muy.demo.controladores;

import com.muy.demo.modelosdto.CreateAdvisoryRequest;
import com.muy.demo.modelosdto.UpdateAdvisoryStatusRequest;
import com.muy.demo.models.Advisory;
import com.muy.demo.seguridad.AuthUtil;
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

    // EXTERNAL crea asesoría: ya no manda externalUserId, sale del token
    @PostMapping
    @PreAuthorize("hasRole('EXTERNAL')")
    public ResponseEntity<Advisory> create(@Valid @RequestBody CreateAdvisoryRequest req) {
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.createAsExternal(email, req));
    }

    // PROGRAMMER confirma/rechaza SOLO lo suyo
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('PROGRAMMER')")
    public ResponseEntity<Advisory> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateAdvisoryStatusRequest req) {
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.updateStatusAsProgrammer(email, id, req));
    }

    // Historial del programador autenticado
    @GetMapping("/me/programmer")
    @PreAuthorize("hasRole('PROGRAMMER')")
    public ResponseEntity<List<Advisory>> myAsProgrammer() {
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.listByProgrammerEmail(email));
    }

    // Historial del externo autenticado
    @GetMapping("/me/external")
    @PreAuthorize("hasRole('EXTERNAL')")
    public ResponseEntity<List<Advisory>> myAsExternal() {
        String email = AuthUtil.currentEmail();
        return ResponseEntity.ok(service.listByExternalEmail(email));
    }
}
