package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.UserSession;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserSessionJpa extends JpaRepository<UserSession, Long> {
    /***
     * Setting time of revoke as now for only members of that family for active sessions.
     * @param id
     * @return
     */
    List<UserSession> findAllByFamilyId(UUID id);

    Optional<UserSession> findByTokenHash(String tokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM UserSession s WHERE s.tokenHash = :tokenHash")

    Optional<UserSession> findByTokenHashWithLock(@Param("tokenHash") String tokenHash);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(" UPDATE UserSession s SET s.revokedAt = :now WHERE s.familyId = :familyId AND s.revokedAt IS NULL")
    int revokeAllByFamilyId( @Param("familyId") UUID familyId, @Param("now") Instant now);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(" UPDATE UserSession s SET s.revokedAt = :now WHERE s.person.id = :personId AND s.revokedAt IS NULL")
    int revokeAllByPersonId(@Param("personId") Long id, @Param("now") Instant now);

}
