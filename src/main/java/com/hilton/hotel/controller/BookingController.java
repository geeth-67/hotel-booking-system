package com.hilton.hotel.controller;

import com.hilton.hotel.domain.BookingStatus;
import com.hilton.hotel.dto.request.BookingRequest;
import com.hilton.hotel.dto.response.BookingResponse;
import com.hilton.hotel.service.impl.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookingService.getAllBookings(page, size));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('GUEST') and @bookingSecurity.isOwner(#id))")
    public ResponseEntity<BookingResponse> getBooking(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('GUEST') and @bookingSecurity.isOwner(#id)")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status
    ) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}
