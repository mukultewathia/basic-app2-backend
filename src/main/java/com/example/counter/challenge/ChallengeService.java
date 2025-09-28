package com.example.counter.challenge;

import com.example.counter.user.User;
import com.example.counter.user.UserRepository;
import com.example.counter.habit.Habit;
import com.example.counter.habit.HabitEntry;
import com.example.counter.habit.HabitRepository;
import com.example.counter.habit.HabitEntryRepository;
import com.example.counter.challenge.ChallengeDto.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepo;
    private final ChallengeHabitsRepository challengeHabitsRepo;
    private final HabitRepository habitRepo;
    private final HabitEntryRepository habitEntryRepo;
    private final UserRepository userRepo;

    public ChallengeService(ChallengeRepository challengeRepo, 
                          ChallengeHabitsRepository challengeHabitsRepo,
                          HabitRepository habitRepo,
                          HabitEntryRepository habitEntryRepo,
                          UserRepository userRepo) {
        this.challengeRepo = challengeRepo;
        this.challengeHabitsRepo = challengeHabitsRepo;
        this.habitRepo = habitRepo;
        this.habitEntryRepo = habitEntryRepo;
        this.userRepo = userRepo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ChallengeResponse createChallenge(String username, ChallengeCreateRequest request) {
        return executeWithTiming("create challenge", () -> {
            System.out.println("createChallengeRequest: " + request.toString());

            User user = validateAndGetUser(username);
            validateChallengeUniqueness(username, request.name(), request.startDate());
            List<Habit> habits = validateAndGetHabits(username, request.habitIds());

            // Create challenge
            Challenge challenge = new Challenge(user, request.name(), request.startDate(), request.durationDays());
            challenge = challengeRepo.save(challenge);

            // Add habits to challenge
            for (Habit habit : habits) {
                ChallengeHabits challengeHabit = new ChallengeHabits(challenge, habit);
                challengeHabitsRepo.save(challengeHabit);
            }

            return new ChallengeResponse(challenge);
        });
    }

    @Transactional
    public List<ChallengeSummaryResponse> getChallengesByStatus(String username, ChallengeStatus status) {
        return executeWithTiming("get challenges by status", () -> {
            List<Challenge> challenges;
            if (status != null) {
                challenges = challengeRepo.findByUsernameAndStatus(username, status);
            } else {
                challenges = challengeRepo.findByUsernameAndNotDeleted(username);
            }

            // Update status of scheduled and active challenges based on current date
            updateChallengeStatuses(challenges);

            return challenges.stream()
                    .map(ChallengeSummaryResponse::new)
                    .toList();
        });
    }

    public ChallengeDetailResponse getChallengeDetails(String username, Long challengeId) {
        return executeWithTiming("get challenge details", () -> {
            Challenge challenge = validateAndGetChallenge(username, challengeId);
            List<Habit> habits = challengeHabitsRepo.findHabitsByChallengeId(challengeId);
            List<HabitInfo> habitsInfo = processHabitEntriesForChallenge(challenge, habits);
            
            return new ChallengeDetailResponse(challenge, habitsInfo);
        });
    }

    @Transactional
    public ChallengeResponse updateChallenge(String username, Long challengeId, ChallengeUpdateRequest request) {
        return executeWithTiming("update challenge", () -> {
            Challenge challenge = validateAndGetChallenge(username, challengeId);

            boolean updated = false;

            if (request.name() != null && !request.name().trim().isEmpty()) {
                challenge.setName(request.name());
                updated = true;
            }

            if (request.startDate() != null) {
                challenge.setStartDate(request.startDate());
                updated = true;
            }

            if (request.durationDays() != null) {
                challenge.setDurationDays(request.durationDays());
                updated = true;
            }

            if (updated) {
                challenge = challengeRepo.save(challenge);
            }

            return new ChallengeResponse(challenge);
        });
    }

    @Transactional
    public void deleteChallenge(String username, Long challengeId) {
        executeWithTiming("delete challenge", () -> {
            Challenge challenge = validateAndGetChallenge(username, challengeId);
            // Soft delete by setting status to deleted
            challenge.setScheduleStatus(ChallengeStatus.deleted);
            challengeRepo.save(challenge);
        });
    }

    @Transactional
    public void addHabitToChallenge(String username, Long challengeId, Long habitId) {
        executeWithTiming("add habit to challenge", () -> {
            Challenge challenge = validateAndGetChallenge(username, challengeId);
            Habit habit = validateAndGetHabit(username, habitId);

            // Check if habit is already in challenge
            Optional<ChallengeHabits> existing = challengeHabitsRepo.findByChallengeIdAndHabitId(challengeId, habitId);
            if (existing.isPresent()) {
                throw new RuntimeException("Habit is already added to this challenge");
            }

            // Add habit to challenge
            ChallengeHabits challengeHabit = new ChallengeHabits(challenge, habit);
            challengeHabitsRepo.save(challengeHabit);
        });
    }

    @Transactional
    public void deleteHabitFromChallenge(String username, Long challengeId, Long habitId) {
        executeWithTiming("delete habit from challenge", () -> {
            validateAndGetChallenge(username, challengeId);
            validateAndGetHabit(username, habitId);

            // Check if habit is in challenge
            Optional<ChallengeHabits> existing = challengeHabitsRepo.findByChallengeIdAndHabitId(challengeId, habitId);
            if (existing.isEmpty()) {
                throw new RuntimeException("Habit is not in this challenge");
            }

            // Remove habit from challenge
            challengeHabitsRepo.deleteByChallengeIdAndHabitId(challengeId, habitId);
        });
    }

    // ==================== HELPER METHODS ====================
    
    /**
     * Validates that a user exists and returns the user entity
     */
    private User validateAndGetUser(String username) {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    
    /**
     * Validates that a challenge exists and belongs to the user, returns the challenge entity
     */
    private Challenge validateAndGetChallenge(String username, Long challengeId) {
        return challengeRepo.findByUsernameAndChallengeIdAndNotDeleted(username, challengeId)
            .orElseThrow(() -> new RuntimeException("Challenge not found with id: " + challengeId));
    }
    
    /**
     * Validates that a habit exists and belongs to the user, returns the habit entity
     */
    private Habit validateAndGetHabit(String username, Long habitId) {
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found with id: " + habitId));
        
        if (!habit.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Habit does not belong to user: " + username);
        }
        
        return habit;
    }
    
    /**
     * Validates that multiple habits exist and belong to the user
     */
    private List<Habit> validateAndGetHabits(String username, List<Long> habitIds) {
        List<Habit> habits = habitRepo.findHabitsByIdsAndUsername(habitIds, username);
        if (habits.size() != habitIds.size()) {
            throw new RuntimeException("One or more habit IDs do not exist or do not belong to user: " + username);
        }
        return habits;
    }
    
    /**
     * Checks if a challenge with the same name and start date already exists
     */
    private void validateChallengeUniqueness(String username, String name, java.time.LocalDate startDate) {
        Optional<Challenge> existingChallenge = challengeRepo.findByUsernameAndNameAndStartDateAndNotDeleted(
            username, name, startDate);
        if (existingChallenge.isPresent()) {
            throw new RuntimeException("Challenge with name '" + name + 
                "' and start date '" + startDate + "' already exists for user: " + username);
        }
    }
    
    /**
     * Processes habit entries for a challenge, filtering by date range and grouping by habit
     */
    private List<HabitInfo> processHabitEntriesForChallenge(Challenge challenge, List<Habit> habits) {
        // Get all habit entries for these habits within the challenge date range
        List<Long> habitIds = habits.stream().map(Habit::getHabitId).toList();
        List<HabitEntry> allEntries = habitEntryRepo.findByHabitIds(habitIds);
        
        // Filter entries within challenge date range
        List<HabitEntry> challengeEntries = allEntries.stream()
            .filter(entry -> !entry.getEntryDate().isBefore(challenge.getStartDate()) && 
                           !entry.getEntryDate().isAfter(challenge.getEndDate()))
            .toList();

        // Group entries by habit
        return habits.stream()
            .map(habit -> {
                List<HabitEntry> habitEntries = challengeEntries.stream()
                    .filter(entry -> entry.getHabit().getHabitId().equals(habit.getHabitId()))
                    .toList();
                return new HabitInfo(habit, habitEntries);
            })
            .toList();
    }
    
    /**
     * Wrapper for performance logging - executes a function and logs the time taken
     */
    private <T> T executeWithTiming(String operationName, java.util.function.Supplier<T> operation) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            return operation.get();
        } finally {
            stopWatch.stop();
            logTime(operationName + " took ms = ", stopWatch);
        }
    }
    
    /**
     * Wrapper for performance logging - executes a void function and logs the time taken
     */
    private void executeWithTiming(String operationName, Runnable operation) {
        executeWithTiming(operationName, () -> {
            operation.run();
            return null;
        });
    }
    
    /**
     * Updates challenge statuses based on current date in India timezone
     * Only updates scheduled and active challenges that need status changes
     */
    private void updateChallengeStatuses(List<Challenge> challenges) {
        // Get current date in India timezone
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(indiaZone);
        
        boolean hasUpdates = false;
        
        for (Challenge challenge : challenges) {
            // Only update scheduled and active challenges
            if (challenge.getScheduleStatus() == ChallengeStatus.scheduled || 
                challenge.getScheduleStatus() == ChallengeStatus.active) {
                
                ChallengeStatus newStatus = determineChallengeStatus(challenge, today);
                
                // Only update if status has changed
                if (challenge.getScheduleStatus() != newStatus) {
                    challenge.setScheduleStatus(newStatus);
                    hasUpdates = true;
                    System.out.println("Updated challenge '" + challenge.getName() + 
                        "' status from " + challenge.getScheduleStatus() + " to " + newStatus);
                }
            }
        }
        
        // Save all updated challenges in one batch
        if (hasUpdates) {
            challengeRepo.saveAll(challenges);
        }
    }
    
    /**
     * Determines the appropriate status for a challenge based on current date
     */
    private ChallengeStatus determineChallengeStatus(Challenge challenge, LocalDate today) {
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();
        
        if (startDate.isAfter(today)) {
            return ChallengeStatus.scheduled;
        } else if (startDate.isEqual(today) || (startDate.isBefore(today) && endDate.isAfter(today)) || endDate.isEqual(today)) {
            return ChallengeStatus.active;
        } else {
            return ChallengeStatus.expired;
        }
    }

    /**
     * Logs the time taken for an operation
     */
    private void logTime(String message, StopWatch stopWatch) {
        System.out.println(message + stopWatch.getTotalTimeMillis() + " ms");
    }
}



