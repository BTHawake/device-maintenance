package method.dto;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import method.Priority;


public record RepairOrderRequest(@NotBlank String equipment_code,@NotBlank String site_id,@NotBlank String issue_summary,@NotNull Priority priority) {


}

