package com.hilton.hotel.dto.response;

import com.hilton.hotel.domain.BookingStatus;
import com.hilton.hotel.domain.RoomType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long bookingId;
    private Long guestId;
    private String guestName;
    private String guestEmail;
    private Long roomId;
    private String roomNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
