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
    private LocalDate entryDate;

    @Column(nullable = false)
    private Boolean performed;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public HabitEntry() {
        this.createdAt = getIndiaTimestamp();
    }

    public HabitEntry(Habit habit, LocalDate entryDate, Boolean performed, String notes) {
        this.habit = habit;
        this.entryDate = entryDate;
        this.performed = performed;
        this.notes = notes;
        this.createdAt = getIndiaTimestamp();
    }
    
    private OffsetDateTime getIndiaTimestamp() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return OffsetDateTime.now(indiaZone);
    }
    
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
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
    
    public LocalDate getEntryDateTimestamp() {
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