package com.kodat.complexhotelsystem.interfaces.Iservices;

import com.kodat.complexhotelsystem.entities.Person;
import com.kodat.complexhotelsystem.entities.ResetPasswordToken;

public interface IResetPasswordService {
    public String generateCode();
    public String encodeCodeForPasswordReset(String code);
    public String saveTokenToDbAndReturnCodeForUser(String email);
    public ResetPasswordToken createToken(String hashedCode, Person person);
    public boolean checkForCorrectCodeProvidedByUser(String providedCode, String email);
    public void setNewPasswordForPerson(String providedCode, String email, String newPassword);
}
