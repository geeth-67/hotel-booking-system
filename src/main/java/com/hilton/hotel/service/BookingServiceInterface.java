package com.hilton.hotel.service;

import com.hilton.hotel.domain.Booking;
import com.hilton.hotel.domain.BookingStatus;
import com.hilton.hotel.domain.Guest;
import com.hilton.hotel.domain.Room;
import com.hilton.hotel.dto.request.BookingRequest;
import com.hilton.hotel.dto.response.BookingResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookingServiceInterface {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse cancelBooking(Long id);

    BookingResponse getBooking(Long id);

    List<BookingResponse> getMyBookings();

    Page<BookingResponse> getAllBookings(int page, int size);

    BookingResponse updateBookingStatus(Long id, BookingStatus status);

    default Booking toEntity(BookingRequest request, Guest guest, Room room) {
        return Booking.builder()
                .guest(guest)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(BookingStatus.PENDING)
                .build();
    }

    default BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .guestId(booking.getGuest().getId())
                .guestName(booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName())
                .guestEmail(booking.getGuest().getEmail())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getType())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
