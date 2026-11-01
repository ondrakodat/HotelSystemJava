package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface IResetPasswordTokenJpa extends JpaRepository<ResetPasswordToken, Long> {
    public Optional<ResetPasswordToken> findByPerson_Email(String email);
    void deleteAllByExpirationBefore(Instant now);
    void deleteAllByUsedAtIsNotNull();
}
