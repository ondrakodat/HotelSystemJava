package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.UserSession;

public interface IUserSessionRepository {
    void saveRefreshToken(UserSession session);
}
