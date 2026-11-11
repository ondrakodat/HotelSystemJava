package com.kodat.complexhotelsystem.interfaces.Iservices;

import com.kodat.complexhotelsystem.dtos.PersonDto;

public interface IUserService {
    PersonDto findUserByEmail(String email);
}
