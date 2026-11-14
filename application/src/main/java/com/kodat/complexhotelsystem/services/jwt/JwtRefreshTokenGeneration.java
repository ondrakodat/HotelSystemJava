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

    private final static SecureRandom SECURE_RANDOM =  new SecureRandom();
    private static final Duration REFRESH_TOKEN_LIFETIME = Duration.ofDays(1);
    private static final Duration REMEMBER_ME_REFRESH_TOKEN_LIFETIME = Duration.ofDays(15);

    public String generateRandomToken(){
        byte [] randomBytes = new byte[64];

        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String createHashToken(String token){

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available rn", e);
        }
    }
    @Transactional
    public String createRefreshToken(Person person, boolean rememberMe){
        Duration lifetime = rememberMe
                ? REMEMBER_ME_REFRESH_TOKEN_LIFETIME
                : REFRESH_TOKEN_LIFETIME;

        String refreshToken = generateRandomToken();
        String tokenHash = createHashToken(refreshToken);

        UserSession session = new UserSession();
        session.setPerson(person);
        session.setToken_hash(tokenHash);
        session.setFamily_id(UUID.randomUUID());

        Instant now = Instant.now();

        session.setCreated_at(now);
        session.setExpires_at(now.plus(lifetime));

        repository.saveRefreshSession(session);

        return refreshToken;
    }

    public UserSession validateRefreshToken(String refreshToken) {

        String tokenHash = createHashToken(refreshToken);

        UserSession session = repository.findSessionByHashToken(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (session.getRevoked_at() != null) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (!session.getExpires_at().isAfter(Instant.now())) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return session;
    }

    public String refresh(String refreshToken) {

        RefreshTokenRotationResultDto result =
                rotationTransaction.rotate(refreshToken);

        if (result.reuseDetected()) {
            reuseService.revokeTokenFamily(result.familyId());
            throw new IllegalArgumentException("Refresh token reuse detected");
        }

        if (result.refreshToken() == null) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        return result.refreshToken();
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
