package com.example.bankingsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the data for a single check entry.
 */
public record CheckData(
        // The number of the check
        String checkNumber,
        // The payee of the check
        String payee,
        // The amount of the check
        double amount,
        // The date of the check
        LocalDate date,
        // The memo of the check
        String memo,
        // The status of the check
        String status, // e.g., "Clear", "Flagged", "Pending"
        // The username of the user who entered the check
        String username, 
        // The timestamp of when the check was entered
        LocalDateTime timestamp
) {} 