package com.example.counter.habit;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Entity
@Table(name = "habit_entries")
public class HabitEntry {
    @Id
    @Column(name = "entry_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "entry_date", nullable = false)
    private OffsetDateTime entryDate;

    @Column(nullable = false)
    private Boolean performed;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // Constructors
    public HabitEntry() {
        this.createdAt = getIndiaTimestamp();
    }

    public HabitEntry(Habit habit, LocalDate entryDate, Boolean performed, String notes) {
        this.habit = habit;
        this.entryDate = convertToIndiaTimestamp(entryDate);
        this.performed = performed;
        this.notes = notes;
        this.createdAt = getIndiaTimestamp();
    }
    
    // Helper method to get current timestamp in India timezone
    private OffsetDateTime getIndiaTimestamp() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return OffsetDateTime.now(indiaZone);
    }
    
    // Helper method to convert LocalDate to OffsetDateTime with India timezone
    private OffsetDateTime convertToIndiaTimestamp(LocalDate localDate) {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return localDate.atStartOfDay(indiaZone).toOffsetDateTime();
    }

    // Getters and setters
    public Long getEntryId() {
        return entryId;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    // Returns the date portion in India timezone
    public LocalDate getEntryDate() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return entryDate.atZoneSameInstant(indiaZone).toLocalDate();
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = convertToIndiaTimestamp(entryDate);
    }
    
    // Internal getter for the actual OffsetDateTime (for database operations)
    public OffsetDateTime getEntryDateTimestamp() {
        return entryDate;
    }

    public Boolean getPerformed() {
        return performed;
    }

    public void setPerformed(Boolean performed) {
        this.performed = performed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 