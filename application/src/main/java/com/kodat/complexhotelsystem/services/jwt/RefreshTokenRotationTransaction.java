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
    private final JwtRefreshTokenGeneration tokenGeneration;

    @Transactional
    public RefreshTokenRotationResultDto rotate(String refreshToken) {

        String tokenHash = tokenGeneration.createHashToken(refreshToken);

        UserSession oldSession = repository.findByTokenHashWithLock(tokenHash)
                .orElse(null);

        if (oldSession == null) {
            return new RefreshTokenRotationResultDto(null, null, false);
        }

        UUID familyId = oldSession.getFamily_id();

        if (oldSession.getRevoked_at() != null) {
            return new RefreshTokenRotationResultDto(null, familyId, true);
        }

        Instant now = Instant.now();

        if (!oldSession.getExpires_at().isAfter(now)) {
            return new RefreshTokenRotationResultDto(null, familyId, false);
        }

        String newRefreshToken = tokenGeneration.generateRandomToken();

        UserSession newSession = new UserSession();
        newSession.setPerson(oldSession.getPerson());
        newSession.setToken_hash(tokenGeneration.createHashToken(newRefreshToken));
        newSession.setFamily_id(familyId);
        newSession.setCreated_at(now);
        newSession.setExpires_at(oldSession.getExpires_at());

        oldSession.setRevoked_at(now);

        repository.saveRefreshSession(oldSession);
        repository.saveRefreshSession(newSession);

        return new RefreshTokenRotationResultDto(newRefreshToken, familyId, false);}
}
