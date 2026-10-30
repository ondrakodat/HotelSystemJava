package com.kodat.complexhotelsystem.services.passwordReset;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Irepository.IResetPasswordRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IResetPasswordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IResetPasswordService {
    private final PasswordEncoder encoder;
    private final IResetPasswordRepository resetPasswordRepository;
    private final IPersonRepository personRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateCode() {
        int max = random.nextInt(1_000_000);
        return String.format("%06d", max);
    }

    @Override
    public String encodeCodeForPasswordReset(String code) {
        if(code == null || code.isBlank())
            throw new RuntimeException("Code input for ecnode cannot be empty!");
        else
            return encoder.encode(code);
    }

    @Override
    public String saveTokenToDbAndReturnCodeForUser(String email) {
        String generatedCode = generateCode();
        ResetPasswordToken token = createToken(encodeCodeForPasswordReset(generatedCode), personRepository.findPersonByEmail(email));
        resetPasswordRepository.saveTokenToDatabase(token);
        return generatedCode;
    }

    @Override
    public ResetPasswordToken createToken(String hashedCode, Person person) {
        return ResetPasswordToken.builder()
                .hashToken(hashedCode)
                .expiration(Instant.now().plus(5, ChronoUnit.MINUTES))
                .usedAt(null)
                .person(person)
                .build();
    }

    @Override
    public boolean checkForCorrectCodeProvidedByUser(String providedCode, String email) {
        Person person = personRepository.findPersonByEmail(email);
        if(person == null)
            throw new RuntimeException("Cant find user by email in password reset");
        ResetPasswordToken token = person.getToken();
        if(token == null)
            throw new RuntimeException("Token want generated for this email yet");
        if(!encoder.matches(providedCode, token.getHashToken()) || token.getExpiration().isBefore(Instant.now()) || token.getUsedAt() != null)
            return false;
        else
            return true;
    }

    @Override
    @Transactional
    public void setNewPasswordForPerson(String providedCode, String email, String newPassword) {
        if(checkForCorrectCodeProvidedByUser(providedCode, email)){
            Person personForEdit = personRepository.findPersonByEmail(email);
            personForEdit.setSecret(encoder.encode(newPassword));
            ResetPasswordToken token = personForEdit.getToken();
            token.setUsedAt(Instant.now());
            resetPasswordRepository.saveTokenToDatabase(token);
            personRepository.editPerson(personForEdit);
        }else{
            throw new RuntimeException("Code doesnt match");
        }
    }


}
