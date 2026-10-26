package method.dto;
import jakarta.validation.constraints.NotBlank;

import method.Priority;


public record RepairOrderRequest(@NotBlank String equipment_code,@NotBlank String site_id,@NotBlank String issue_summary, Priority priority) {


}

