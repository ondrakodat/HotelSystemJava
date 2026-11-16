package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.dtos.RefreshTokenRotationResultDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRefreshTokenGenerationTest {
    @Mock IUserSessionRepository repository;
    @Mock RefreshTokenRotationTransaction rotationTransaction;
    @Mock RefreshTokenReuseService reuseService;
    RefreshTokenUtils utils;
    JwtRefreshTokenGeneration service;

    @BeforeEach
    void setUp() {
        service = new JwtRefreshTokenGeneration(repository, rotationTransaction, reuseService, utils);
    }

    @Test
    void generatedTokensHaveExpectedEntropyEncodingAndAreDistinct() {
        Set<String> tokens = IntStream.range(0, 64)
                .mapToObj(ignored -> utils.generateRandomToken())
                .collect(java.util.stream.Collectors.toSet());

        assertEquals(64, tokens.size());
        tokens.forEach(token -> {
            assertEquals(86, token.length(), "64 random bytes encode to 86 unpadded Base64URL characters");
            assertTrue(token.matches("[A-Za-z0-9_-]{86}"));
        });
    }

    @Test
    void storesOnlySha256HexHashOfRefreshToken() throws Exception {
        Person person = new Person();
        String token = service.createRefreshToken(person, false);
        ArgumentCaptor<UserSession> sessionCaptor = ArgumentCaptor.forClass(UserSession.class);
        verify(repository).saveRefreshSession(sessionCaptor.capture());

        String storedHash = sessionCaptor.getValue().getTokenHash();
        String expectedHash = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));

        assertEquals(expectedHash, storedHash);
        assertNotEquals(token, storedHash);
        assertEquals(64, storedHash.length());
        assertTrue(storedHash.matches("[0-9a-f]{64}"));
        assertSame(person, sessionCaptor.getValue().getPerson());
        assertNotNull(sessionCaptor.getValue().getFamilyId());
    }

    @Test
    void rejectsNullAndBlankTokensWithoutCallingRotation() {
        assertThrows(IllegalArgumentException.class, () -> service.refresh(null));
        assertThrows(IllegalArgumentException.class, () -> service.refresh("  "));
        verifyNoInteractions(rotationTransaction, reuseService);
    }

    @Test
    void rejectsMissingOrExpiredRotationResult() {
        when(rotationTransaction.rotate("bad-token"))
                .thenReturn(new RefreshTokenRotationResultDto(null, null, false, null, null));
        assertThrows(IllegalArgumentException.class, () -> service.refresh("bad-token"));

        verifyNoInteractions(reuseService);
    }

    @Test
    void detectsReuseAndRevokesWholeTokenFamily() {
        UUID familyId = UUID.randomUUID();
        when(rotationTransaction.rotate("reused-token"))
                .thenReturn(new RefreshTokenRotationResultDto(null, familyId, true, null, null));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> service.refresh("reused-token"));

        assertTrue(thrown.getMessage().contains("reuse"));
        verify(reuseService).revokeTokenFamily(familyId);
    }

    @Test
    void logoutRevokesOnlyFamilyIdentifiedByPresentedRefreshToken() {
        UUID familyId = UUID.randomUUID();
        UserSession matchingSession = new UserSession();
        matchingSession.setFamilyId(familyId);
        when(repository.findSessionByHashToken(any())).thenReturn(java.util.Optional.of(matchingSession));

        service.revokeRefreshToken("presented-token");

        verify(repository).revokeAllByFamilyId(eq(familyId), any(Instant.class));
    }

    @Test
    void logoutWithoutRefreshTokenIsNoOp() {
        service.revokeRefreshToken(null);
        service.revokeRefreshToken("");

        verifyNoInteractions(repository);
    }
}
