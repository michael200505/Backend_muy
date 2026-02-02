package com.muy.demo.modelosdto;

import com.muy.demo.models.Modality;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateAdvisoryRequest {
    @NotNull public Long programmerId;
    @NotNull public Long externalUserId;
    @NotNull public LocalDateTime startAt;
    @NotNull public LocalDateTime endAt;
    @NotNull public Modality modality;
    public String topic;
}
