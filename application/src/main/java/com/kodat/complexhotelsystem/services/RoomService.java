package com.kodat.complexhotelsystem.services;

import com.kodat.complexhotelsystem.dtos.RoomDto;
import com.kodat.complexhotelsystem.entities.Room;
import com.kodat.complexhotelsystem.interfaces.Irepository.IRoomRepository;
import com.kodat.complexhotelsystem.interfaces.Iservices.IRoomService;
import com.kodat.complexhotelsystem.interfaces.Iservices.Imapper.IRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoomService implements IRoomService {
    private final IRoomMapper mapper;
    private final IRoomRepository repository;

    @Override
    public RoomDto createOrEditRoom(Room room) {
        return mapper
                .toDto(repository
                        .crateOrEditRoom(room)
                );
    }
}
