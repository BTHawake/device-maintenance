package com.awake.repair.dto;

//统一响应体
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String timestamp
) {
}