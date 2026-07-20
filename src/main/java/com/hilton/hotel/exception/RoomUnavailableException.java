package com.hilton.hotel.exception;

public class RoomUnavailableException extends RuntimeException {

    public RoomUnavailableException(String message) {
        super(message);
    }
}
