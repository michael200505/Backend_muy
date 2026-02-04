package com.muy.demo.modelosdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProjectRequest {
    @NotBlank public String title;
    public String description;
    public String repoUrl;
    public String demoUrl;
    @NotNull public Long programmerId;
}
