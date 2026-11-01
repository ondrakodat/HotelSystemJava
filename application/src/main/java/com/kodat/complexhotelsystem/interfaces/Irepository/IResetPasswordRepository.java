package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.ResetPasswordToken;

import java.time.Instant;

public interface IResetPasswordRepository {
    public ResetPasswordToken getTokenByEmail(String email);
    public void saveTokenToDatabase(ResetPasswordToken token);
    void deleteAllByExpirationBefore(Instant now);
    void deleteAllByUsedAtIsNotNull();
}
