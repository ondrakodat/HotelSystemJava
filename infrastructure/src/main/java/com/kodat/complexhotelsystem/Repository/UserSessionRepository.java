package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IUserSessionJpa;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserSessionRepository implements IUserSessionRepository {
    private final IUserSessionJpa jpaReposiroty;

    @Override
    public void saveRefreshToken(UserSession session) {
        jpaReposiroty.save(session);
    }
}
