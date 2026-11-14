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

    @BeforeEach
    void setUp() {
        transaction = new RefreshTokenRotationTransaction(repository, tokenGeneration);
        familyId = UUID.randomUUID();
        originalExpiry = Instant.now().plusSeconds(3600);
        Person person = new Person();
        person.setEmail("user@example.test");
        oldSession = new UserSession();
        oldSession.setPerson(person);
        oldSession.setFamily_id(familyId);
        oldSession.setToken_hash("a".repeat(64));
        oldSession.setCreated_at(Instant.now().minusSeconds(60));
        oldSession.setExpires_at(originalExpiry);
        when(tokenGeneration.createHashToken("old-token")).thenReturn("a".repeat(64));
    }

    @Test
    void rotatesValidTokenAndKeepsFamilyAndAbsoluteExpiry() {
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.of(oldSession));
        when(tokenGeneration.generateRandomToken()).thenReturn("new-token");
        when(tokenGeneration.createHashToken("new-token")).thenReturn("b".repeat(64));

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertEquals("new-token", result.refreshToken());
        assertEquals("user@example.test", result.email());
        assertEquals(familyId, result.familyId());
        assertEquals(originalExpiry, result.expiresAt());
        assertNotNull(oldSession.getRevoked_at());

        ArgumentCaptor<UserSession> saved = ArgumentCaptor.forClass(UserSession.class);
        verify(repository, times(2)).saveRefreshSession(saved.capture());
        UserSession replacement = saved.getAllValues().stream()
                .filter(session -> "b".repeat(64).equals(session.getToken_hash()))
                .findFirst().orElseThrow();
        assertEquals(familyId, replacement.getFamily_id());
        assertEquals(originalExpiry, replacement.getExpires_at());
        assertEquals("b".repeat(64), replacement.getToken_hash());
        assertNotEquals("new-token", replacement.getToken_hash());
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
        oldSession.setRevoked_at(Instant.now().minusSeconds(10));
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.of(oldSession));

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertTrue(result.reuseDetected());
        assertEquals(familyId, result.familyId());
        verify(repository, never()).saveRefreshSession(any());
    }

    @Test
    void expiredTokenDoesNotRotate() {
        oldSession.setExpires_at(Instant.now().minusSeconds(1));
        when(repository.findByTokenHashWithLock("a".repeat(64))).thenReturn(Optional.of(oldSession));

        RefreshTokenRotationResultDto result = transaction.rotate("old-token");

        assertNull(result.refreshToken());
        assertFalse(result.reuseDetected());
        verify(repository, never()).saveRefreshSession(any());
    }
}
