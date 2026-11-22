package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.GoogleUserDto;
import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.exceptions.GoogleEmailIsNotVerified;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleOAuth2ServiceTest {
    private static final String GOOGLE_ID = "google-subject-123";
    private static final String EMAIL = "guest@example.test";

    @Mock IPersonRepository repository;
    @Mock PasswordEncoder encoder;

    private GoogleOAuth2Service service;

    @BeforeEach
    void setUp() {
        service = new GoogleOAuth2Service(repository, encoder);
    }

    @Test
    void rejectsGoogleAccountWithUnverifiedEmail() {
        GoogleUserDto dto = googleUser(false);

        assertThrows(GoogleEmailIsNotVerified.class, () -> service.processGoogleUser(dto));

        verifyNoInteractions(repository, encoder);
    }

    @Test
    void returnsPersonAlreadyLinkedByGoogleId() {
        Person linkedPerson = new Person();
        linkedPerson.setGoogleId(GOOGLE_ID);
        when(repository.findByGoogleId(GOOGLE_ID)).thenReturn(linkedPerson);

        Person result = service.processGoogleUser(googleUser(true));

        assertSame(linkedPerson, result);
        verify(repository, never()).existsByEmail(anyString());
        verify(repository, never()).editPerson(any());
        verify(repository, never()).createPerson(any());
    }

    @Test
    void linksExistingLocalPersonByVerifiedEmail() {
        Person localPerson = new Person();
        localPerson.setEmail(EMAIL);
        when(repository.findByGoogleId(GOOGLE_ID)).thenReturn(null);
        when(repository.existsByEmail(EMAIL)).thenReturn(true);
        when(repository.findPersonByEmail(EMAIL)).thenReturn(localPerson);

        Person result = service.processGoogleUser(googleUser(true));

        assertSame(localPerson, result);
        assertEquals(GOOGLE_ID, localPerson.getGoogleId());
        verify(repository).editPerson(localPerson);
        verify(repository, never()).createPerson(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void createsNewPersonForPreviouslyUnknownGoogleAccount() {
        when(repository.findByGoogleId(GOOGLE_ID)).thenReturn(null);
        when(repository.existsByEmail(EMAIL)).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encoded-random-password");
        when(repository.createPerson(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.processGoogleUser(googleUser(true));

        ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
        verify(repository).createPerson(personCaptor.capture());
        Person createdPerson = personCaptor.getValue();

        assertSame(createdPerson, result);
        assertEquals(GOOGLE_ID, createdPerson.getGoogleId());
        assertEquals(EMAIL, createdPerson.getEmail());
        assertEquals("Alex", createdPerson.getFirstname());
        assertEquals("Tester", createdPerson.getLastname());
        assertEquals("encoded-random-password", createdPerson.getPassword());
        verify(encoder).encode(anyString());
    }

    private GoogleUserDto googleUser(boolean emailVerified) {
        return new GoogleUserDto(
                GOOGLE_ID,
                EMAIL,
                "Alex",
                "Tester",
                emailVerified
        );
    }
}
