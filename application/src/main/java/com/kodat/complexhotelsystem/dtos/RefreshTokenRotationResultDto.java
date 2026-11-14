package com.kodat.complexhotelsystem.dtos;

import java.util.UUID;

public record RefreshTokenRotationResultDto(
        String refreshToken,
        UUID familyId,
        boolean reuseDetected) {
}
