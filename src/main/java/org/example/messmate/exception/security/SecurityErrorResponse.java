package org.example.messmate.exception.security;

public record SecurityErrorResponse(
        String code,
        String message
) {
}
