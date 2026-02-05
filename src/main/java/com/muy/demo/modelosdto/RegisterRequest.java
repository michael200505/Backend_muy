package com.muy.demo.modelosdto;

import com.muy.demo.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterRequest {
    @NotBlank public String fullName;
    @Email @NotBlank public String email;
    @NotBlank public String password;
    public String phone;
    @NotNull public Role role; // ADMIN crea PROGRAMMER; EXTERNAL se auto-registra
}
