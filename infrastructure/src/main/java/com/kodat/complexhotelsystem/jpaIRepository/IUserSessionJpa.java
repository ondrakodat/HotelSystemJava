package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserSessionJpa extends JpaRepository<UserSession, Long> {
    List<UserSession> findAllByFamily_id(UUID id);
    Optional<UserSession> findByToken_hash(String tokenHash);

}
