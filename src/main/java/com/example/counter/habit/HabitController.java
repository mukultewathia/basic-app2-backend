package com.example.counter.habit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.counter.habit.HabitDto.HabitRequest;
import com.example.counter.habit.HabitDto.HabitResponse;
import com.example.counter.habit.HabitDto.HabitEntryRequest;
import com.example.counter.habit.HabitDto.HabitEntryResponse;
import com.example.counter.habit.HabitDto.AllHabitData;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*")
public class HabitController {
    @Autowired
    private HabitService habitService;

    // 1. addHabit(username, habitName, description)
    @PostMapping("/addHabit")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponse addHabit(@Valid @RequestBody HabitRequest req) {
        Habit habit = habitService.addHabit(
                req.username(),
                req.habitName(),
                req.description()
        );
        return new HabitResponse(habit);
    }

    // 2. getAllHabitEntries(username, list of <habitNames>)
    @GetMapping("/allHabitEntries")
    public List<HabitEntryResponse> getAllHabitEntries(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "habitNames", required = true) List<String> habitNames) {
        return habitService.getAllHabitEntries(username, habitNames);
    }

    // 3. deleteHabit(entry_id) - Note: This deletes a habit entry, not the habit itself
    @DeleteMapping("/deleteHabitEntry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabitEntry(@RequestParam(name = "entryId") Long entryId) {
        habitService.deleteHabitEntry(entryId);
    }

    // 4. addHabitEntry(username, habitName, entryDate, performed, notes)
    @PostMapping("/addHabitEntry")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitEntryResponse addHabitEntry(@Valid @RequestBody HabitEntryRequest req) {
        System.out.println("req = " + req.toString());
        HabitEntry entry = habitService.addHabitEntry(
                req.username(),
                req.habitName(),
                req.entryDate(),
                req.performed(),
                req.notes()
        );
        return new HabitEntryResponse(entry);
    }

    // 5. getAllHabit(habitName, username) - Gets all habits for a user, optionally filtered by habit name
    @GetMapping("/allHabits")
    public List<AllHabitData> getAllHabits(
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "habitName", required = false) String habitName) {
        return habitService.getAllHabits(username, habitName);
    }
} 