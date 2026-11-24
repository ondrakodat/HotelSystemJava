package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoomJpa extends JpaRepository<Room, Long> {


}
