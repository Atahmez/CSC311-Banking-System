package com.example.bankingsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents metadata for a saved report.
 */
public record SavedReportData(
    // Used to uniquely identify the report
    String reportId,
    // The type of report
    String reportType,
    // The date range of the report
    LocalDate startDate,
    LocalDate endDate,
    // The status filter of the report
    String statusFilter,
    // The timestamp of when the report was generated
    LocalDateTime generationTimestamp,
    // The username of the user who generated the report
    String generatedByUsername
    // We might add a filename or path if reports were actually saved to disk
) {
    // Custom toString for better display in ListView
    @Override
    public String toString() {
        return String.format("%s (%s to %s) - Generated: %s",
                reportType,
                startDate, 
                endDate,
                generationTimestamp.format(java.time.format.DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT))
        );
    }
} 