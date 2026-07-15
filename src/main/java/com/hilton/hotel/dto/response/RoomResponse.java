package com.hilton.hotel.dto.response;

import com.hilton.hotel.domain.RoomStatus;
import com.hilton.hotel.domain.RoomType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private RoomStatus status;
    private String description;
    private LocalDateTime createdAt;
}
