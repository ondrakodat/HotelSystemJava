package com.kodat.complexhotelsystem.interfaces.Iservices;

import com.kodat.complexhotelsystem.dtos.RoomDto;
import com.kodat.complexhotelsystem.entities.Room;

public interface IRoomService {
    RoomDto createOrEditRoom(Room room);
}
