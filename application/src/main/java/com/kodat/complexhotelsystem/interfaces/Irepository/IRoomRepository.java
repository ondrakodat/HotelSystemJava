package com.kodat.complexhotelsystem.interfaces.Irepository;

import com.kodat.complexhotelsystem.entities.Room;
import com.kodat.complexhotelsystem.enums.RoomState;
import com.kodat.complexhotelsystem.enums.RoomType;

import java.util.List;

public interface IRoomRepository {
    Room crateOrEditRoom(Room room);
    List<Room> findRoomsByTypeAndCapacity(RoomType type, int capacity, RoomState state);

}
