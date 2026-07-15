package com.hilton.hotel.dto.response;

import com.hilton.hotel.domain.Guest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private LocalDateTime createdAt;

    public static GuestResponse from(Guest guest) {
        return GuestResponse.builder()
                .id(guest.getId())
                .firstName(guest.getFirstName())
                .lastName(guest.getLastName())
                .email(guest.getEmail())
                .phoneNo(guest.getPhoneNo())
                .createdAt(guest.getCreatedAt())
                .build();
    }
}