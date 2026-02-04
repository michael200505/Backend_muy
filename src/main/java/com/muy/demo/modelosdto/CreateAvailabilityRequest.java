package com.muy.demo.modelosdto;

import com.muy.demo.models.Modality;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class CreateAvailabilityRequest {
    @NotNull public Long programmerId;
    @NotNull public DayOfWeek dayOfWeek;
    @NotNull public LocalTime startTime;
    @NotNull public LocalTime endTime;
    @NotNull public Modality modality;
}
