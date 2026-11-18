package com.kodat.complexhotelsystem.services.passwordReset;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import com.kodat.complexhotelsystem.exceptions.InvalidResetCodeException;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Irepository.IResetPasswordRepository;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    private static final String EMAIL = "guest@example.test";

    @Mock PasswordEncoder encoder;
    @Mock IResetPasswordRepository resetPasswordRepository;
    @Mock IPersonRepository personRepository;
    @Mock IUserSessionRepository sessionRepository;

    private PasswordResetService service;
    private Person person;
    private ResetPasswordToken token;

    @BeforeEach
    void setUp() {
        service = new PasswordResetService(
                encoder,
                resetPasswordRepository,
                personRepository,
                sessionRepository
        );

        person = new Person();
        person.setId(42L);
        person.setEmail(EMAIL);

        token = new ResetPasswordToken();
        token.setHashToken("stored-hash");
        token.setExpiration(Instant.now().plusSeconds(300));
        token.setFailedAttempts(0);
        person.setToken(token);

        when(personRepository.findPersonByEmail(EMAIL)).thenReturn(person);
    }

    @Test
    void invalidCodeIncrementsAndSavesFailedAttemptBeforeThrowing() {
        when(encoder.matches("123456", "stored-hash")).thenReturn(false);

        assertThrows(
                InvalidResetCodeException.class,
                () -> service.setNewPasswordForPerson("123456", EMAIL, "StrongPassword123!")
        );

        assertEquals(1, token.getFailedAttempts());
        verify(resetPasswordRepository).saveTokenToDatabase(token);
        verifyNoInteractions(sessionRepository);
        verify(personRepository, never()).editPerson(any());
    }

    @Test
    void doesNotCheckOrIncrementCodeAfterFiveFailedAttempts() {
        token.setFailedAttempts(5);

        assertThrows(
                InvalidResetCodeException.class,
                () -> service.setNewPasswordForPerson("123456", EMAIL, "StrongPassword123!")
        );

        assertEquals(5, token.getFailedAttempts());
        verifyNoInteractions(encoder, resetPasswordRepository, sessionRepository);
        verify(personRepository, never()).editPerson(any());
    }

    @Test
    void invalidCodeExceptionIsConfiguredNotToRollBackAttemptCount() throws Exception {
        Transactional transaction = PasswordResetService.class
                .getMethod("setNewPasswordForPerson", String.class, String.class, String.class)
                .getAnnotation(Transactional.class);

        assertNotNull(transaction);
        assertTrue(Arrays.asList(transaction.dontRollbackOn()).contains(InvalidResetCodeException.class));
    }

    @Test
    void correctCodeChangesPasswordMarksCodeUsedAndRevokesSessions() {
        when(encoder.matches("123456", "stored-hash")).thenReturn(true);
        when(encoder.encode("StrongPassword123!")).thenReturn("new-password-hash");

        service.setNewPasswordForPerson("123456", EMAIL, "StrongPassword123!");

        assertEquals("new-password-hash", person.getPassword());
        assertNotNull(token.getUsedAt());
        verify(resetPasswordRepository).saveTokenToDatabase(token);
        verify(personRepository).editPerson(person);
        verify(sessionRepository).revokeAllByPersonId(eq(42L), any(Instant.class));
    }
}
