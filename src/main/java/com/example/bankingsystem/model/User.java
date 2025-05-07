package com.example.bankingsystem.model;

import java.time.LocalDateTime;

/**
 * Represents a user account in the system.
 */
public record User(
        String username, // functionally equivalent to email
        String passwordHash,
        LocalDateTime createdAt
) {
}