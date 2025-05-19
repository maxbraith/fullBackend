package com.max_griffin_project.dto;

public record AuthResponseDto(
        String token,
        String refreshToken,
        String expiresIn
) {}
