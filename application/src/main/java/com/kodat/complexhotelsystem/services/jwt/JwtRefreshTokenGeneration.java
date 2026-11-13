package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import jakarta.transaction.Transactional;
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
    private final static SecureRandom SECURE_RANDOM =  new SecureRandom();
    private static final Duration REFRESH_TOKEN_LIFETIME = Duration.ofDays(1);
    private static final Duration REMEMBER_ME_REFRESH_TOKEN_LIFETIME = Duration.ofDays(15);

    private String generateRandomToken(){
        byte [] randomBytes = new byte[64];

        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String createHashToken(String token){

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
        session.setCreated_at(Instant.now());
        session.setPerson(person);
        session.setToken_hash(tokenHash);
        session.setFamily_id(UUID.randomUUID());
        session.setExpires_at(Instant.now().plus(lifetime));

        repository.saveRefreshToken(session);

        return refreshToken;
    }

}
