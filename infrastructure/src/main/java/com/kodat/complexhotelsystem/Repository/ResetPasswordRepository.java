package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import com.kodat.complexhotelsystem.interfaces.Irepository.IResetPasswordRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IResetPasswordTokenJpa;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@AllArgsConstructor
public class ResetPasswordRepository implements IResetPasswordRepository {
    private final IResetPasswordTokenJpa repositoryJpa;

    public ResetPasswordToken getTokenByEmail(String email){
        return repositoryJpa.findByPerson_Email(email).orElse(null);
    }

    @Override
    public void saveTokenToDatabase(ResetPasswordToken token) {
       repositoryJpa.save(token);
    }

    @Override
    public void deleteAllByExpirationBefore(Instant now) {
        repositoryJpa.deleteAllByExpirationBefore(now);
    }

    @Override
    public void deleteAllByUsedAtIsNotNull() {
        repositoryJpa.deleteAllByUsedAtIsNotNull();
    }
}
