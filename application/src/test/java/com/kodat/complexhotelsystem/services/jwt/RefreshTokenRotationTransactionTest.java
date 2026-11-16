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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRotationTransactionTest {
    @Mock IUserSessionRepository repository;
    @Mock JwtRefreshTokenGeneration tokenGeneration;

    RefreshTokenRotationTransaction transaction;
    UserSession oldSession;
    UUID familyId;
    Instant originalExpiry;
    RefreshTokenUtils utils;

    @BeforeEach
    void setUp() {
        transaction = new RefreshTokenRotationTransaction(repository, utils);
        familyId = UUID.randomUUID();
        originalExpiry = Instant.now().plusSeconds(3600);
        Person person = new Person();
        person.setEmail("user@example.test");
        oldSession = new UserSession();
        oldSession.setPerson(person);
        oldSession.setFamilyId(familyId);
        oldSession.setTokenHash("a".repeat(64));
        oldSession.setCreatedAt(Instant.now().minusSeconds(60));
        oldSession.setExpiresAt(originalExpiry);
        when(utils.createHashToken("old-token")).thenReturn("a".repeat(64));
    }

    @Test
    void rotatesValidTokenAndKeepsFamilyAndAbsoluteExpiry() {
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.of(oldSession));
        when(utils.generateRandomToken()).thenReturn("new-token");
        when(utils.createHashToken("new-token")).thenReturn("b".repeat(64));

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertEquals("new-token", result.refreshToken());
        assertEquals("user@example.test", result.email());
        assertEquals(familyId, result.familyId());
        assertEquals(originalExpiry, result.expiresAt());
        assertNotNull(oldSession.getRevokedAt());

        ArgumentCaptor<UserSession> saved = ArgumentCaptor.forClass(UserSession.class);
        verify(repository, times(2)).saveRefreshSession(saved.capture());
        UserSession replacement = saved.getAllValues().stream()
                .filter(session -> "b".repeat(64).equals(session.getTokenHash()))
                .findFirst().orElseThrow();
        assertEquals(familyId, replacement.getFamilyId());
        assertEquals(originalExpiry, replacement.getExpiresAt());
        assertEquals("b".repeat(64), replacement.getTokenHash());
        assertNotEquals("new-token", replacement.getTokenHash());
    }

    @Test
    void missingTokenIsRejectedByResultAndDoesNotWrite() {
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.empty());

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertNull(result.refreshToken());
        assertFalse(result.reuseDetected());
        verify(repository, never()).saveRefreshSession(any());
    }

    @Test
    void revokedTokenIsReportedAsReuse() {
        oldSession.setRevokedAt(Instant.now().minusSeconds(10));
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.of(oldSession));

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertTrue(result.reuseDetected());
        assertEquals(familyId, result.familyId());
        verify(repository, never()).saveRefreshSession(any());
    }

    @Test
    void expiredTokenDoesNotRotate() {
        oldSession.setExpiresAt(Instant.now().minusSeconds(1));
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.of(oldSession));

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertNull(result.refreshToken());
        assertFalse(result.reuseDetected());
        verify(repository, never()).saveRefreshSession(any());
    }
}
