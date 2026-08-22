package com.peluqueria.backend.media.exceptions;

public class InvalidMediaFileException extends RuntimeException {
    public InvalidMediaFileException(String message) {
        super(message);
    }
}
