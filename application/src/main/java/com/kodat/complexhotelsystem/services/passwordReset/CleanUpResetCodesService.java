package com.kodat.complexhotelsystem.services.passwordReset;

import com.kodat.complexhotelsystem.interfaces.Irepository.IResetPasswordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanUpResetCodesService {
    private final PasswordResetService cleanup;

    @Scheduled(fixedRate = 60_000)
    public void cleanUpTokens(){
        cleanup.cleanUpTokens();
    }
}
