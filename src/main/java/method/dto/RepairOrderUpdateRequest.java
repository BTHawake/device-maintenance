package method.dto;

import method.Priority;

public record RepairOrderUpdateRequest(
        String status,
        Priority priority,
        String issueSummary
) {
}