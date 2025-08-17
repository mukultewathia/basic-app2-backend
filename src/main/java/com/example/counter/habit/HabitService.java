package com.example.counter.habit;

import com.example.counter.user.User;
import com.example.counter.user.UserRepository;
import com.example.counter.habit.HabitDto.AllHabitData;
import com.example.counter.habit.HabitDto.HabitEntryResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {
    private final HabitRepository habitRepo;
    private final HabitEntryRepository habitEntryRepo;
    private final UserRepository userRepo;

    public HabitService(HabitRepository habitRepo, HabitEntryRepository habitEntryRepo, UserRepository userRepo) {
        this.habitRepo = habitRepo;
        this.habitEntryRepo = habitEntryRepo;
        this.userRepo = userRepo;
    }

    public Habit addHabit(String username, String habitName, String description) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        User user = userRepo.findByUsername(username).orElseThrow(
            () -> new RuntimeException("User not found: " + username)
        );

        Optional<Habit> existingHabit = habitRepo.findByUsernameAndName(username, habitName);
        if (existingHabit.isPresent()) {
            throw new RuntimeException("Habit '" + habitName + "' already exists for user: " + username);
        }

        Habit habit = new Habit(user, habitName, description);
        Habit saved = habitRepo.save(habit);

        stopWatch.stop();
        logTime("add habit took ms = ", stopWatch);
        return saved;
    }

    public List<HabitEntryResponse> getAllHabitEntries(String username, List<String> habitNames) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<HabitEntry> entries = habitEntryRepo.findByUsernameAndHabitNames(username, habitNames);

        stopWatch.stop();
        logTime("get all habit entries took ms = ", stopWatch);
        
        return entries.stream()
                .map(HabitEntryResponse::new)
                .toList();
    }

    public void deleteHabitEntry(String username, Long entryId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        HabitEntry entry = habitEntryRepo.findById(entryId)
            .orElseThrow(() -> new RuntimeException("Habit entry not found with id: " + entryId));
        
        if (!entry.getHabit().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Habit entry does not belong to user: " + username);
        }

        habitEntryRepo.deleteById(entryId);

        stopWatch.stop();
        logTime("delete habit entry took ms = ", stopWatch);
    }

    // Adds a habit entry
    // LocalDate to India Timezone Conversion:
    // - API accepts LocalDate (e.g., "2024-01-15") for user-friendly input
    // - Internally converts to OffsetDateTime with Asia/Kolkata timezone at 00:00:00
    // - Stores as TIMESTAMPTZ in database (e.g., "2024-01-15 00:00:00+05:30")
    // - When retrieving, converts back to LocalDate in India timezone
    // - Ensures consistent date handling regardless of server timezone
    public HabitEntry addHabitEntry(String username, String habitName, LocalDate entryDate, Boolean performed, String notes) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (notes == null) {
            notes = "";
        }

        LocalDate today = LocalDate.now();
        if (entryDate.isAfter(today)) {
            throw new RuntimeException("Cannot create habit entry for future date: " + entryDate);
        }

        Habit habit = habitRepo.findByUsernameAndName(username, habitName)
            .orElseThrow(() -> new RuntimeException("Habit not found: " + habitName + " for user: " + username));

        Optional<HabitEntry> existingEntry = habitEntryRepo.findByUsernameAndHabitNameAndDate(username, habitName, entryDate);
        if (existingEntry.isPresent()) {
            HabitEntry entry = existingEntry.get();
            entry.setPerformed(performed);
            entry.setNotes(notes);
            HabitEntry saved = habitEntryRepo.save(entry);
            
            stopWatch.stop();
            logTime("update habit entry took ms = ", stopWatch);
            return saved;
        } else {
            HabitEntry entry = new HabitEntry(habit, entryDate, performed, notes);
            HabitEntry saved = habitEntryRepo.save(entry);
            
            stopWatch.stop();
            logTime("add habit entry took ms = ", stopWatch);
            return saved;
        }
    }

    // Gets all habits for a user, optionally filtered by habit name
    public List<AllHabitData> getAllHabits(String username, String habitName) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Habit> habits;
        if (habitName != null && !habitName.trim().isEmpty()) {
            Optional<Habit> habit = habitRepo.findByUsernameAndName(username, habitName);
            habits = habit.map(List::of).orElse(List.of());
        } else {
            habits = habitRepo.findByUsername(username);
        }

        stopWatch.stop();
        logTime("get all habits took ms = ", stopWatch);

        return habits.stream()
                .map(AllHabitData::new)
                .toList();
    }

    public void deleteHabit(String username, Long habitId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        // Check if habit exists and belongs to the user
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found with id: " + habitId));
        
        if (!habit.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Habit does not belong to user: " + username);
        }
        
        // Delete the habit (JPA will cascade delete all associated entries)
        habitRepo.delete(habit);

        stopWatch.stop();
        logTime("delete habit took ms = ", stopWatch);
    }

    private void logTime(String message, StopWatch stopWatch) {
        System.out.println(message + stopWatch.getTotalTimeMillis() + " ms");
    }
} 