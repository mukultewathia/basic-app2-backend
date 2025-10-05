package com.example.counter.habit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class HabitDto {
    
    public record HabitRequest(
            @NotNull @NotBlank String habitName,
            String description) {}
    
    public record HabitResponse(
            Long habitId,
            Long userId,
            String name,
            String description,
            OffsetDateTime createdAt) {
        public HabitResponse(Habit habit) {
            this(habit.getHabitId(), 
                 habit.getUser().getUserId(),
                 habit.getName(),
                 habit.getDescription(),
                 habit.getCreatedAt());
        }
    }
    
    public record HabitEntryRequest(
            @NotNull @NotBlank String habitName,
            @NotNull LocalDate entryDate,
            @NotNull Boolean performed,
            String notes) {}
    
    public record HabitEntryResponse(
            Long entryId,
            Long habitId,
            String habitName,
            LocalDate entryDate,
            Boolean performed,
            String notes,
            OffsetDateTime createdAt) {
        public HabitEntryResponse(HabitEntry entry) {
            this(entry.getEntryId(),
                 entry.getHabit().getHabitId(),
                 entry.getHabit().getName(),
                 entry.getEntryDate(),
                 entry.getPerformed(),
                 entry.getNotes(),
                 entry.getCreatedAt());
        }
    }
    
    public record AllHabitData(
            Long habitId,
            String name,
            String description,
            OffsetDateTime createdAt) {
        public AllHabitData(Habit habit) {
            this(habit.getHabitId(),
                 habit.getName(),
                 habit.getDescription(),
                 habit.getCreatedAt());
        }
    }


} 