package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IResetPasswordTokenJpa extends JpaRepository<ResetPasswordToken, Long> {
    public <Optional>ResetPasswordToken findResetPasswordTokenByEmail(String email);
}
