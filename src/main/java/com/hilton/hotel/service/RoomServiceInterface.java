package com.hilton.hotel.service;

import com.hilton.hotel.domain.Room;
import com.hilton.hotel.domain.RoomStatus;
import com.hilton.hotel.dto.request.RoomRequest;
import com.hilton.hotel.dto.response.RoomResponse;
import org.springframework.data.domain.Page;

public interface RoomServiceInterface {
    RoomResponse createRoom(RoomRequest request);

    Page<RoomResponse> getAllRooms(
            int page,
            int size
    );

    Page<RoomResponse> getSearchAllRooms(
            String keyword,
            int page,
            int size
    );

    RoomResponse getRoomById(Long id);

    Page<RoomResponse> getRoomsByStatus(
            RoomStatus status,
            int page,
            int size
    );

    RoomResponse updateRoom(
            Long id,
            RoomRequest request
    );

    RoomResponse updateRoomStatus(
            Long id,
            RoomStatus status
    );

    void deleteRoom(Long id);

    default RoomResponse toResponse(Room room) {

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .status(room.getStatus())
                .description(room.getDescription())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
