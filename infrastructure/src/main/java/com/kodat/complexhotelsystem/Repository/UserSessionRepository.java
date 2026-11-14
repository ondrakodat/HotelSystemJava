package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.UserSession;
import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IUserSessionJpa;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class UserSessionRepository implements IUserSessionRepository {
    private final IUserSessionJpa jpaReposiroty;

    @Override
    public void saveRefreshSession(UserSession session) {
        jpaReposiroty.save(session);
    }
    @Override
    public Optional<UserSession> findSessionByHashToken(String token){
       return jpaReposiroty.findByToken_hash(token);
    }

    @Override
    public Optional<UserSession> findByTokenHashWithLock(String tokenHash) {
        return jpaReposiroty.findByTokenHashWithLock(tokenHash);
    }

    @Transactional
    @Override
    public List<UserSession> findAllSessionsInOneFamily(UUID id){
        return jpaReposiroty.findAllByFamily_id(id);
    }

    @Override
    public int revokeAllByFamilyId(UUID familyId, Instant now) {
        return jpaReposiroty.revokeAllByFamilyId(familyId, now);
    }

}
