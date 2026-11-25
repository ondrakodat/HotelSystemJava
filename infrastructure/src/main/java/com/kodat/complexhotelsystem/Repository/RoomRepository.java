package com.kodat.complexhotelsystem.Repository;

import com.kodat.complexhotelsystem.entities.Room;
import com.kodat.complexhotelsystem.enums.RoomState;
import com.kodat.complexhotelsystem.enums.RoomType;
import com.kodat.complexhotelsystem.interfaces.Irepository.IRoomRepository;
import com.kodat.complexhotelsystem.jpaIRepository.IRoomJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RoomRepository implements IRoomRepository {
    private final IRoomJpa jpaRepository;

    @Override
    public Room crateOrEditRoom(Room room) {
        return jpaRepository.save(room);
    }

    @Override
    public List<Room> findRoomsByTypeAndCapacity(RoomType type, int capacity, RoomState state) {
        return jpaRepository.findByTypeAndCapacityGreaterThanEqualAndState(type, capacity, state);
    }
}
