package com.hilton.hotel.service.impl;

import com.hilton.hotel.domain.Room;
import com.hilton.hotel.domain.RoomStatus;
import com.hilton.hotel.dto.request.RoomRequest;
import com.hilton.hotel.dto.response.RoomResponse;
import com.hilton.hotel.exception.DuplicateResourceException;
import com.hilton.hotel.exception.ResourceNotFoundException;
import com.hilton.hotel.repository.RoomRepository;
import com.hilton.hotel.service.RoomServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService implements RoomServiceInterface {

    private final RoomRepository roomRepository;

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException(
                    "Room already exists with number: " + request.getRoomNumber()
            );
        }

        Room room = Room.builder()

                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();

        return toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAllRooms(
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return roomRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getSearchAllRooms(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.searchRooms(keyword, pageable)
                .map(this::toResponse);
    }

    @Override
    public RoomResponse getRoomById(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room",
                                id.toString()
                        ));

        return toResponse(room);
    }

    @Override
    public Page<RoomResponse> getRoomsByStatus(
            RoomStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return roomRepository.findByStatus(status, pageable)
                .map(this::toResponse);
    }

    @Override
    public RoomResponse updateRoom(
            Long id,
            RoomRequest request
    ) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room",
                                id.toString()
                        ));

        if (!room.getRoomNumber().equals(request.getRoomNumber())
                && roomRepository.existsByRoomNumber(request.getRoomNumber())) {

            throw new DuplicateResourceException(
                    "Room already exists with number: "
                            + request.getRoomNumber()
            );
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setStatus(request.getStatus());
        room.setDescription(request.getDescription());

        return toResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse updateRoomStatus(
            Long id,
            RoomStatus status
    ) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room",
                                id.toString()
                        ));

        room.setStatus(status);

        return toResponse(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room",
                                id.toString()
                        ));
        roomRepository.delete(room);
    }
}