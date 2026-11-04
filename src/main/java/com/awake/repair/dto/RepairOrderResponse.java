package com.awake.repair.dto;

public record RepairOrderResponse(
        String id,
        String equipment_code,
        String status, String priority,
        String created_at) {
}