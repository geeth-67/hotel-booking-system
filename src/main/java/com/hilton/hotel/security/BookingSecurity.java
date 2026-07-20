package com.hilton.hotel.security;

import com.hilton.hotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("bookingSecurity")
@RequiredArgsConstructor
public class BookingSecurity {

    private final BookingRepository bookingRepository;

    public boolean isOwner(Long bookingId) {
        String keycloakId = extractKeycloakId();
        if (keycloakId == null) {
            return false;
        }

        if (!bookingRepository.existsById(bookingId)) {
            return true;
        }

        return bookingRepository.existsByIdAndGuest_KeycloakId(bookingId, keycloakId);
    }

    private String extractKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        return jwt.getSubject();
    }
}
