package com.muy.demo.servicios;

import com.muy.demo.modelosdto.*;
import com.muy.demo.models.Role;
import com.muy.demo.models.User;
import com.muy.demo.repositorios.UserRepository;
import com.muy.demo.seguridad.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse login(AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email, req.password));
        return new AuthResponse(jwt.generateToken(req.email));
    }

   public void register(RegisterRequest req) {
    if (users.existsByEmail(req.email)) throw new IllegalArgumentException("Email ya registrado");

    // regla simple: EXTERNAL puede auto-registrarse; PROGRAMMER/ADMIN deberían crearse desde ADMIN (lo controlas en Controller)
    User u = new User();
    u.setFullName(req.fullName);
    u.setEmail(req.email);

    // ✅ AGREGA ESTO:
    u.setPhone(req.phone);

    u.setPasswordHash(encoder.encode(req.password));
    u.setRole(req.role != null ? req.role : Role.EXTERNAL);
    users.save(u);
}


    // ✅ NUEVO: fuerza EXTERNAL y reutiliza register()
    public void registerExternal(RegisterRequest req) {
        // fuerza EXTERNAL
        req.role = com.muy.demo.models.Role.EXTERNAL;
        register(req);
    }

    // ✅ NUEVO: creación de usuario por ADMIN (solo PROGRAMMER o ADMIN)
    public void adminCreateUser(AdminCreateUserRequest req) {
        if (users.existsByEmail(req.email)) throw new IllegalArgumentException("Email ya registrado");

        if (req.role != com.muy.demo.models.Role.PROGRAMMER && req.role != com.muy.demo.models.Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN solo puede crear PROGRAMMER o ADMIN");
        }

        com.muy.demo.models.User u = new com.muy.demo.models.User();
        u.setFullName(req.fullName);
        u.setEmail(req.email);
        u.setPasswordHash(encoder.encode(req.password));
        u.setRole(req.role);
        users.save(u);
    }
}
