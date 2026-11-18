package com.kodat.complexhotelsystem;

import com.kodat.complexhotelsystem.Repository.PersonRepository;
import com.kodat.complexhotelsystem.Repository.ResetPasswordRepository;
import com.kodat.complexhotelsystem.Repository.UserSessionRepository;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.jpaIRepository.IPersonJpa;
import com.kodat.complexhotelsystem.jpaIRepository.IResetPasswordTokenJpa;
import com.kodat.complexhotelsystem.jpaIRepository.IUserSessionJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfrastructureRepositoryAdapterTest {
    @Mock IPersonJpa personJpa;
    @Mock IResetPasswordTokenJpa resetTokenJpa;
    @Mock IUserSessionJpa userSessionJpa;

    private PersonRepository people;
    private ResetPasswordRepository resetTokens;
    private UserSessionRepository sessions;

    @BeforeEach
    void setUp() {
        people = new PersonRepository(personJpa);
        resetTokens = new ResetPasswordRepository(resetTokenJpa);
        sessions = new UserSessionRepository(userSessionJpa);
    }

    @Test
    void personRepositoryDelegatesLookupsAndWrites() {
        Person person = new Person();
        when(personJpa.findPersonByEmail("guest@example.test")).thenReturn(person);
        when(personJpa.findById(12L)).thenReturn(Optional.of(person));
        when(personJpa.save(person)).thenReturn(person);
        when(personJpa.existsByEmail("guest@example.test")).thenReturn(true);
        when(personJpa.existsByPhone("+420123456789")).thenReturn(true);

        assertSame(person, people.findPersonByEmail("guest@example.test"));
        assertSame(person, people.findPersonById(12L));
        assertSame(person, people.createPerson(person));
        people.editPerson(person);
        assertTrue(people.existsByEmail("guest@example.test"));
        assertTrue(people.existsByPhone("+420123456789"));

        verify(personJpa, times(2)).save(person);
    }

    @Test
    void personRepositoryReportsMissingIdAndEmptyList() {
        when(personJpa.findById(404L)).thenReturn(Optional.empty());
        when(personJpa.findAll()).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> people.findPersonById(404L));
        assertTrue(people.findAllPersons().isEmpty());
    }

    @Test
    void personRepositoryWrapsNonEmptyList() {
        List<Person> found = List.of(new Person(), new Person());
        when(personJpa.findAll()).thenReturn(found);

        assertEquals(Optional.of(found), people.findAllPersons());
    }

    @Test
    void resetTokenRepositoryMapsMissingTokenAndDelegatesWritesAndCleanup() {
        ResetPasswordToken token = new ResetPasswordToken();
        Instant now = Instant.now();
        when(resetTokenJpa.findByPerson_Email("guest@example.test")).thenReturn(Optional.of(token));

        assertSame(token, resetTokens.getTokenByEmail("guest@example.test"));
        resetTokens.saveTokenToDatabase(token);
        resetTokens.deleteAllByExpirationBefore(now);
        resetTokens.deleteAllByUsedAtIsNotNull();

        when(resetTokenJpa.findByPerson_Email("missing@example.test")).thenReturn(Optional.empty());
        assertNull(resetTokens.getTokenByEmail("missing@example.test"));
        verify(resetTokenJpa).save(token);
        verify(resetTokenJpa).deleteAllByExpirationBefore(now);
        verify(resetTokenJpa).deleteAllByUsedAtIsNotNull();
    }

    @Test
    void sessionRepositoryDelegatesTokenQueriesAndRevocation() {
        UserSession session = new UserSession();
        UUID familyId = UUID.randomUUID();
        Instant now = Instant.now();
        when(userSessionJpa.findByTokenHash("token-hash")).thenReturn(Optional.of(session));
        when(userSessionJpa.findByTokenHashWithLock("token-hash")).thenReturn(Optional.of(session));
        when(userSessionJpa.findAllByFamilyId(familyId)).thenReturn(List.of(session));
        when(userSessionJpa.revokeAllByFamilyId(familyId, now)).thenReturn(2);
        when(userSessionJpa.revokeAllByPersonId(12L, now)).thenReturn(1);

        sessions.saveRefreshSession(session);
        assertSame(session, sessions.findSessionByHashToken("token-hash").orElseThrow());
        assertSame(session, sessions.findByTokenHashWithLock("token-hash").orElseThrow());
        assertEquals(List.of(session), sessions.findAllSessionsInOneFamily(familyId));
        assertEquals(2, sessions.revokeAllByFamilyId(familyId, now));
        assertEquals(1, sessions.revokeAllByPersonId(12L, now));

        verify(userSessionJpa).save(session);
    }
}
