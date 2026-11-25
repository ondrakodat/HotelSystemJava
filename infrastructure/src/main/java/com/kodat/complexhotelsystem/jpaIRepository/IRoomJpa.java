package com.kodat.complexhotelsystem.jpaIRepository;

import com.kodat.complexhotelsystem.entities.Room;
import com.kodat.complexhotelsystem.enums.RoomState;
import com.kodat.complexhotelsystem.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRoomJpa extends JpaRepository<Room, Long> {
    List<Room> findByTypeAndCapacityGreaterThanEqualAndState(RoomType type, int capacity, RoomState state);

}
