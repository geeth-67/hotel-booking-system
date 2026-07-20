package com.hilton.hotel.service.impl;

import com.hilton.hotel.domain.*;
import com.hilton.hotel.dto.request.BookingRequest;
import com.hilton.hotel.dto.response.BookingResponse;
import com.hilton.hotel.exception.BookingConflictException;
import com.hilton.hotel.exception.InvalidBookingException;
import com.hilton.hotel.exception.ResourceNotFoundException;
import com.hilton.hotel.exception.RoomUnavailableException;
import com.hilton.hotel.repository.BookingRepository;
import com.hilton.hotel.repository.GuestRepository;
import com.hilton.hotel.repository.RoomRepository;
import com.hilton.hotel.service.BookingServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService implements BookingServiceInterface {

    private static final Set<BookingStatus> ACTIVE_STATUSES = EnumSet.of(
            BookingStatus.PENDING,
            BookingStatus.CONFIRMED,
            BookingStatus.CHECKED_IN
    );

    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Guest guest = resolveAuthenticatedGuest();
        Room room = findRoomOrThrow(request.getRoomId());

        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());
        ensureRoomIsAvailable(room);
        preventOverlappingBookings(
                room.getId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                null
        );

        Booking booking = toEntity(request, guest, room);
        Booking saved = bookingRepository.save(booking);
        log.info(
                "Booking created: id={}, guestId={}, roomId={}",
                saved.getId(),
                guest.getId(),
                room.getId()
        );

        return toResponse(saved);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Guest guest = resolveAuthenticatedGuest();
        Booking booking = findBookingOrThrow(id);

        if (!booking.getGuest().getId().equals(guest.getId())) {
            throw new InvalidBookingException(
                    "You can only cancel your own bookings"
            );
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new InvalidBookingException(
                    "Cannot cancel a checked-out booking"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking cancelled: id={}, guestId={}", saved.getId(), guest.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        Booking booking = findBookingOrThrow(id);
        ensureGuestCanAccessBooking(booking);
        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {
        Guest guest = resolveAuthenticatedGuest();

        return bookingRepository.findByGuest_IdOrderByCreatedAtDesc(guest.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return bookingRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        if (status == null) {
            throw new InvalidBookingException("Booking status is required");
        }

        Booking booking = findBookingOrThrow(id);

        if (booking.getStatus() == BookingStatus.CANCELLED
                && status != BookingStatus.CANCELLED) {
            throw new InvalidBookingException(
                    "Cannot change status of a cancelled booking"
            );
        }

        booking.setStatus(status);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking status updated: id={}, status={}", saved.getId(), status);

        return toResponse(saved);
    }

    private void ensureGuestCanAccessBooking(Booking booking) {
        if (hasRole("ROLE_ADMIN")) {
            return;
        }

        Guest guest = resolveAuthenticatedGuest();
        if (!booking.getGuest().getId().equals(guest.getId())) {
            throw new InvalidBookingException(
                    "You can only view your own bookings"
            );
        }
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private Guest resolveAuthenticatedGuest() {
        String keycloakId = extractKeycloakId();

        return guestRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Guest", keycloakId)
                );
    }

    private String extractKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new InvalidBookingException("Authenticated JWT is required");
        }

        return jwt.getSubject();
    }

    private Room findRoomOrThrow(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Room", roomId.toString())
                );
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking", id.toString())
                );
    }

    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            throw new InvalidBookingException("Check-in date cannot be in the past");
        }

        if (checkOutDate.isBefore(today)) {
            throw new InvalidBookingException("Check-out date cannot be in the past");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidBookingException(
                    "Check-out date must be after check-in date"
            );
        }
    }

    private void ensureRoomIsAvailable(Room room) {
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RoomUnavailableException(
                    "Room is not available. Current status: " + room.getStatus()
            );
        }
    }

    private void preventOverlappingBookings(
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Long excludeBookingId
    ) {
        List<Booking> roomBookings = bookingRepository.findByRoomId(roomId);

        boolean hasOverlap = roomBookings.stream()
                .filter(booking -> ACTIVE_STATUSES.contains(booking.getStatus()))
                .filter(booking -> excludeBookingId == null
                        || !booking.getId().equals(excludeBookingId))
                .anyMatch(booking ->
                        checkInDate.isBefore(booking.getCheckOutDate())
                                && checkOutDate.isAfter(booking.getCheckInDate())
                );

        if (hasOverlap) {
            throw new BookingConflictException(
                    "Room already has an overlapping booking for the selected dates"
            );
        }
    }
}
