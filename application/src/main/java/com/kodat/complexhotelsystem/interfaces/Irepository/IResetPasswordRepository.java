package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.ResetPasswordToken;

public interface IResetPasswordRepository {
    public ResetPasswordToken getTokenByEmail(String email);
    public void saveTokenToDatabase(ResetPasswordToken token);
}
