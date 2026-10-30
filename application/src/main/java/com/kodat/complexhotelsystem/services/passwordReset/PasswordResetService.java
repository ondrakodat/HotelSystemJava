package com.kodat.complexhotelsystem.services.passwordReset;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Irepository.IResetPasswordRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IResetPasswordService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@AllArgsConstructor
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
    public void saveTokenToDB(String email) {
        ResetPasswordToken token = createToken(encodeCodeForPasswordReset(generateCode()), personRepository.findPersonByEmail(email));
        resetPasswordRepository.saveTokenToDatabase(token);
    }

    @Override
    public ResetPasswordToken createToken(String hashedCode, Person person) {
        return null;
    }
}
