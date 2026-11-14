package com.kodat.complexhotelsystem.services.jwt;

import com.kodat.complexhotelsystem.interfaces.Irepository.IUserSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenReuseService {
    private final IUserSessionRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeTokenFamily(UUID familyId) {

        repository.revokeAllByFamilyId(familyId, Instant.now());
    }
}
