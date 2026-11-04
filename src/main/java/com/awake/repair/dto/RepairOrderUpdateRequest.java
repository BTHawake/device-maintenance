package com.awake.repair.dto;

import com.awake.repair.enums.Priority;

public record RepairOrderUpdateRequest(
        String status,
        Priority priority,
        String issueSummary
) {
}