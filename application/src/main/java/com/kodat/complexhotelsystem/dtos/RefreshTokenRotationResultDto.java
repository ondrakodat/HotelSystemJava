package com.kodat.complexhotelsystem.dtos;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenRotationResultDto(
        String refreshToken,
        UUID familyId,
        boolean reuseDetected,
        String email,
        Instant expiresAt
) {
}
