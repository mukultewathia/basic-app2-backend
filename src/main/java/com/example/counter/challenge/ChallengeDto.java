package com.example.counter.challenge;

import com.example.counter.habit.Habit;
import com.example.counter.habit.HabitEntry;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class ChallengeDto {
    
    public record ChallengeCreateRequest(
            @NotNull @NotBlank String name,
            @NotNull List<@NotNull Long> habitIds,
            @NotNull LocalDate startDate,
            @NotNull @Positive Integer durationDays) {}
    
    public record ChallengeUpdateRequest(
            String name,
            LocalDate startDate,
            @Min(1) Integer durationDays) {}
    
    public record ChallengeSummaryResponse(
            Long challengeId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            ChallengeStatus scheduleStatus,
            CompletionStatus completionStatus) {
        public ChallengeSummaryResponse(Challenge challenge) {
            this(challenge.getChallengeId(),
                 challenge.getName(),
                 challenge.getStartDate(),
                 challenge.getEndDate(),
                 challenge.getScheduleStatus(),
                 challenge.getCompletion());
        }
    }
    
    public record HabitEntryInfo(
            Long entryId,
            LocalDate entryDate,
            Boolean performed,
            String notes) {
        public HabitEntryInfo(HabitEntry entry) {
            this(entry.getEntryId(),
                 entry.getEntryDate(),
                 entry.getPerformed(),
                 entry.getNotes());
        }
    }
    
    public record HabitInfo(
            Long habitId,
            String habitName,
            String habitDescription,
            List<HabitEntryInfo> habitEntries) {
        public HabitInfo(Habit habit, List<HabitEntry> entries) {
            this(habit.getHabitId(),
                 habit.getName(),
                 habit.getDescription(),
                 entries.stream()
                        .map(HabitEntryInfo::new)
                        .toList());
        }
    }
    
    public record ChallengeDetailResponse(
            Long challengeId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Integer durationDays,
            ChallengeStatus scheduleStatus,
            CompletionStatus completionStatus,
            Integer successPercent,
            List<HabitInfo> habitsInfo,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        public ChallengeDetailResponse(Challenge challenge, List<HabitInfo> habitsInfo) {
            this(challenge.getChallengeId(),
                 challenge.getName(),
                 challenge.getStartDate(),
                 challenge.getEndDate(),
                 challenge.getDurationDays(),
                 challenge.getScheduleStatus(),
                 challenge.getCompletion(),
                 challenge.getSuccessPercent(),
                 habitsInfo,
                 challenge.getCreatedAt(),
                 challenge.getUpdatedAt());
        }
    }
    
    public record ChallengeResponse(
            Long challengeId,
            Long userId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            Integer durationDays,
            ChallengeStatus scheduleStatus,
            CompletionStatus completionStatus,
            Integer successPercent,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        public ChallengeResponse(Challenge challenge) {
            this(challenge.getChallengeId(),
                 challenge.getUser().getUserId(),
                 challenge.getName(),
                 challenge.getStartDate(),
                 challenge.getEndDate(),
                 challenge.getDurationDays(),
                 challenge.getScheduleStatus(),
                 challenge.getCompletion(),
                 challenge.getSuccessPercent(),
                 challenge.getCreatedAt(),
                 challenge.getUpdatedAt());
        }
    }
}



