package com.muy.demo.controladores;

import com.muy.demo.modelosdto.*;
import com.muy.demo.servicios.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    @PostMapping("/register-external")
    public ResponseEntity<?> registerExternal(@Valid @RequestBody RegisterRequest req) {
        auth.registerExternal(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/register-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminCreate(@Valid @RequestBody com.muy.demo.modelosdto.AdminCreateUserRequest req) {
        auth.adminCreateUser(req);
        return ResponseEntity.ok().build();
    }
}
