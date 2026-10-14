package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.interfaces.Irepository.IPersonRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IPersonService;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor
public class PersonService implements IPersonService, UserDetailsService {
    private final IPersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return personRepository.findPersonByEmail(email);
    }
}
