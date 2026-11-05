package com.awake.repair.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import com.awake.repair.enums.Priority;
import jakarta.validation.constraints.Size;


public record RepairOrderRequest(
        @NotBlank String equipment_code,
        @NotBlank String site_id,
        @Size(min=1,max=255)
        @NotBlank String issue_summary,
        @NotNull Priority priority) {
}

