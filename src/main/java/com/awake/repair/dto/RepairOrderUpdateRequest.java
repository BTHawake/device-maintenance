package com.awake.repair.dto;

import com.awake.repair.enums.Priority;
import jakarta.validation.constraints.Size;

public record RepairOrderUpdateRequest(
        String status,
        Priority priority,
        @Size(min=1,max=255)
        String issueSummary
) {
}