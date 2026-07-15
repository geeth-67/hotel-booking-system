package com.hilton.hotel.controller;

import com.hilton.hotel.domain.Guest;
import com.hilton.hotel.dto.request.RegisterRequest;
import com.hilton.hotel.dto.response.GuestResponse;
import com.hilton.hotel.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<GuestResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        Guest guest = registrationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GuestResponse.from(guest));
    }
}