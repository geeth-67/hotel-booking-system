package com.hilton.hotel.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest req
    ) {
        log.info("Not found {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(404, "Not Found", ex.getMessage())
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest req
    ) {
        log.info("Duplicate resource {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse(409, "Duplicate resource", ex.getMessage())
        );
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingConflictException(
            BookingConflictException ex, HttpServletRequest req
    ) {
        log.info("Booking conflict {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse(409, "Booking conflict", ex.getMessage())
        );
    }

    @ExceptionHandler(RoomUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleRoomUnavailableException(
            RoomUnavailableException ex, HttpServletRequest req
    ) {
        log.info("Room unavailable {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse(409, "Room unavailable", ex.getMessage())
        );
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBookingException(
            InvalidBookingException ex, HttpServletRequest req
    ) {
        log.info("Invalid booking {}: {}", req.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(400, "Invalid booking", ex.getMessage())
        );
    }
}

