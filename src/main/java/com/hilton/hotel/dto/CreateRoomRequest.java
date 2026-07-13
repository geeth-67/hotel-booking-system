package com.hilton.hotel.dto;

import com.hilton.hotel.domain.RoomType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 10 , message = "room number cannot exceed 10 characters")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType type;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "1.00", message = "Price per night must be at least 1.00")
    @Digits(integer = 8, fraction = 2, message = "Price per night must have at most 8 digits and 2 decimal places")
    private BigDecimal pricePerNight;

    @NotNull
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 5, message = "Capacity cannot exceed 5")
    private Integer capacity;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
