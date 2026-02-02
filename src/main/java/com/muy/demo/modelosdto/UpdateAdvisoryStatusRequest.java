package com.muy.demo.modelosdto;

import com.muy.demo.models.AdvisoryStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateAdvisoryStatusRequest {
    @NotNull public AdvisoryStatus status;
    public String rejectionReason;
}
