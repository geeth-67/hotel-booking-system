package com.hilton.hotel.repository;

import com.hilton.hotel.domain.Booking;
import com.hilton.hotel.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestId(Long guestId);

    List<Booking> findByGuest_IdOrderByCreatedAtDesc(Long guestId);

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByGuest_KeycloakId(String keycloakId);

    boolean existsByRoomIdAndStatus(Long roomId, BookingStatus status);

    boolean existsByIdAndGuest_KeycloakId(Long id, String keycloakId);

    List<Booking> findByRoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}
