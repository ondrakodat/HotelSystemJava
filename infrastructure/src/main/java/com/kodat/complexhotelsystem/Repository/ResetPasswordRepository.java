package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import com.kodat.complexhotelsystem.interfaces.Irepository.IResetPasswordRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IResetPasswordTokenJpa;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ResetPasswordRepository implements IResetPasswordRepository {
    private final IResetPasswordTokenJpa repositoryJpa;

    public ResetPasswordToken getTokenByEmail(String email){
        return repositoryJpa.findByPerson_Email(email).get();
    }

    @Override
    public void saveTokenToDatabase(ResetPasswordToken token) {
       repositoryJpa.save(token);
    }
}
