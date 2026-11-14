package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.UserSession;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserSessionRepository {
    void saveRefreshSession(UserSession session);
    public List<UserSession> findAllSessionsInOneFamily(UUID id);
    public Optional<UserSession> findSessionByHashToken(String token);
    public Optional<UserSession> findByTokenHashWithLock(String tokenHash);
    int revokeAllByFamilyId(UUID familyId, Instant now);
}
