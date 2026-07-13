package com.hilton.hotel.controller;

import com.hilton.hotel.domain.RoomStatus;
import com.hilton.hotel.dto.RoomRequest;
import com.hilton.hotel.dto.RoomResponse;
import com.hilton.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<Page<RoomResponse>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                roomService.getAllRooms(page, size)
        );
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
            @PathVariable Long roomId
    ) {

        return ResponseEntity.ok(
                roomService.getRoomById(roomId)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody RoomRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(request));
    }

    @PatchMapping("/{roomId}/status")
    public ResponseEntity<RoomResponse> updateRoomStatus(
            @PathVariable Long roomId,
            @RequestParam RoomStatus status
    ) {

        return ResponseEntity.ok(
                roomService.updateRoomStatus(roomId, status)
        );
    }
}