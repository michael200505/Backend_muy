package com.muy.demo.repositorios;

import com.muy.demo.models.Role;
import com.muy.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // ✅ NUEVO: obtener ID del usuario por email
    @Query("select u.id from User u where u.email = :email")
    Long findIdByEmail(String email);

    @Query("select u.role from User u where u.email = :email")
    Role findRoleByEmail(String email);
}
