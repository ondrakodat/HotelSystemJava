package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.dtos.RefreshTokenRotationResultDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtRefreshTokenGeneration {
    private final IUserSessionRepository repository;
    private final RefreshTokenRotationTransaction rotationTransaction;
    private final RefreshTokenReuseService reuseService;
    private final RefreshTokenUtils tokenUtils;
    private static final Duration REFRESH_TOKEN_LIFETIME = Duration.ofDays(1);
    private static final Duration REMEMBER_ME_REFRESH_TOKEN_LIFETIME = Duration.ofDays(15);


    @Transactional
    public String createRefreshToken(Person person, boolean rememberMe){
        Duration lifetime = rememberMe
                ? REMEMBER_ME_REFRESH_TOKEN_LIFETIME
                : REFRESH_TOKEN_LIFETIME;

        String refreshToken = tokenUtils.generateRandomToken();
        String tokenHash = tokenUtils.createHashToken(refreshToken);

        UserSession session = new UserSession();
        session.setPerson(person);
        session.setTokenHash(tokenHash);
        session.setFamilyId(UUID.randomUUID());

        Instant now = Instant.now();

        session.setCreatedAt(now);
        session.setExpiresAt(now.plus(lifetime));

        repository.saveRefreshSession(session);

        return refreshToken;
    }

    public UserSession validateRefreshToken(String refreshToken) {

        String tokenHash = tokenUtils.createHashToken(refreshToken);

        UserSession session = repository.findSessionByHashToken(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (session.getRevokedAt() != null) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (!session.getExpiresAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return session;
    }

    public RefreshTokenRotationResultDto refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshTokenRotationResultDto result =
                rotationTransaction.rotate(refreshToken);

        if (result.reuseDetected()) {
            reuseService.revokeTokenFamily(result.familyId());
            throw new IllegalArgumentException("Refresh token reuse detected");
        }

        if (result.refreshToken() == null) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        return result;
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        String tokenHash = tokenUtils.createHashToken(refreshToken);

        repository.findSessionByHashToken(tokenHash)
                .ifPresent(session ->
                        repository.revokeAllByFamilyId(
                                session.getFamilyId(),
                                Instant.now()
                        )
                );
    }

//    @Transactional
//    public String rotateRefreshToken(String refreshToken) {
//
//        String tokenHash = createHashToken(refreshToken);
//
//        UserSession oldSession = repository.findByTokenHashWithLock(tokenHash)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
//
//        String newRefreshToken = generateRandomToken();
//        String newTokenHash = createHashToken(newRefreshToken);
//
//        UserSession newSession = new UserSession();
//
//        newSession.setPerson(oldSession.getPerson());
//        newSession.setToken_hash(newTokenHash);
//
//        newSession.setFamily_id(oldSession.getFamily_id());
//
//        Instant now = Instant.now();
//
//        if (oldSession.getRevoked_at() != null) {
//            throw new IllegalArgumentException("Refresh token has already been used");
//        }
//
//        if (!oldSession.getExpires_at().isAfter(now)) {
//            throw new IllegalArgumentException("Refresh token has expired");
//        }
//
//        newSession.setCreated_at(now);
//
//        newSession.setExpires_at(oldSession.getExpires_at());
//
//        repository.saveRefreshSession(newSession);
//
//        oldSession.setRevoked_at(now);
//
//        repository.saveRefreshSession(oldSession);
//
//        return newRefreshToken;
//    }

}
