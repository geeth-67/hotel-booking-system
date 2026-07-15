package com.hilton.hotel.dto.request;

import com.hilton.hotel.domain.Room;
import com.hilton.hotel.domain.RoomStatus;
import com.hilton.hotel.domain.RoomType;
import com.hilton.hotel.dto.response.RoomResponse;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private RoomStatus status;
    private String description;

    public static RoomResponse from(Room room){
        return RoomResponse
                .builder()
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