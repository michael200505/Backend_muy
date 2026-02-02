package com.muy.demo.modelosdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    @Email @NotBlank public String email;
    @NotBlank public String password;
}
