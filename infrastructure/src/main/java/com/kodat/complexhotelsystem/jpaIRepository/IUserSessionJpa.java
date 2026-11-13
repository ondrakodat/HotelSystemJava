package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserSessionJpa extends JpaRepository<UserSession, Long> {
}
