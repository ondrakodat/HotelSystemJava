package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.dtos.RefreshTokenRotationResultDto;
import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenRotationTransaction {
    private final IUserSessionRepository repository;
    private final RefreshTokenUtils tokenUtils;

    @Transactional
    public RefreshTokenRotationResultDto rotate(String refreshToken) {

        String tokenHash = tokenUtils.createHashToken(refreshToken);

        UserSession oldSession = repository.findByTokenHashWithLock(tokenHash)
                .orElse(null);

        if (oldSession == null) {
            return new RefreshTokenRotationResultDto(null, null, false, null, null, 0L);
        }

        UUID familyId = oldSession.getFamilyId();

        if (oldSession.getRevokedAt() != null) {
            return new RefreshTokenRotationResultDto(null, familyId, true, null, null, 0L);
        }

        Instant now = Instant.now();

        if (!oldSession.getExpiresAt().isAfter(now)) {
            return new RefreshTokenRotationResultDto(null, familyId, false, null, null, 0L);
        }

        // Údaje potřebné pro vytvoření nového access tokenu a cookie
        String email = oldSession.getPerson().getEmail();
        long tokenVersion = oldSession.getPerson().getTokenVersion();
        Instant expiresAt = oldSession.getExpiresAt();

        String newRefreshToken = tokenUtils.generateRandomToken();

        UserSession newSession = new UserSession();
        newSession.setPerson(oldSession.getPerson());
        newSession.setTokenHash(tokenUtils.createHashToken(newRefreshToken));
        newSession.setFamilyId(familyId);
        newSession.setCreatedAt(now);
        newSession.setExpiresAt(expiresAt);

        oldSession.setRevokedAt(now);

        repository.saveRefreshSession(oldSession);
        repository.saveRefreshSession(newSession);

        return new RefreshTokenRotationResultDto(
                newRefreshToken,
                familyId,
                false,
                email,
                expiresAt,
                tokenVersion
        );
    }

}
