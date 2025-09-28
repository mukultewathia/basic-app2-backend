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
import com.example.counter.habit.HabitDto.NoteRequest;
import com.example.counter.habit.HabitDto.NoteResponse;
import com.example.counter.auth.security.CurrentUser;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*")
public class HabitController {
    @Autowired
    private HabitService habitService;
    
    @Autowired
    private NoteService noteService;

    // 1. addHabit(habitName, description) - username from JWT token
    @PostMapping("/addHabit")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponse addHabit(@Valid @RequestBody HabitRequest req) {
        String username = CurrentUser.getCurrentUsername();
        Habit habit = habitService.addHabit(
                username,
                req.habitName(),
                req.description()
        );
        return new HabitResponse(habit);
    }

    // 2. getAllHabitEntries(list of <habitNames>) - username from JWT token
    @GetMapping("/allHabitEntries")
    public List<HabitEntryResponse> getAllHabitEntries(
            @RequestParam(name = "habitNames", required = true) List<String> habitNames) {
        String username = CurrentUser.getCurrentUsername();
        return habitService.getAllHabitEntries(username, habitNames);
    }

    // 3. deleteHabitEntry(entry_id) - Note: This deletes a habit entry, not the habit itself
    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabitEntry(@PathVariable Long entryId) {
        String username = CurrentUser.getCurrentUsername();
        habitService.deleteHabitEntry(username, entryId);
    }

    // 4. addHabitEntry(habitName, entryDate, performed, notes) - username from JWT token
    @PostMapping("/addHabitEntry")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitEntryResponse addHabitEntry(@Valid @RequestBody HabitEntryRequest req) {
        System.out.println("req = " + req.toString());
        String username = CurrentUser.getCurrentUsername();
        HabitEntry entry = habitService.addHabitEntry(
                username,
                req.habitName(),
                req.entryDate(),
                req.performed(),
                req.notes()
        );
        return new HabitEntryResponse(entry);
    }

    // 5. getAllHabit(habitName) - Gets all habits for current user, optionally filtered by habit name
    @GetMapping("/allHabits")
    public List<AllHabitData> getAllHabits(
            @RequestParam(name = "habitName", required = false) String habitName) {
        String username = CurrentUser.getCurrentUsername();
        System.out.println("tewamaf username: " + username);
        return habitService.getAllHabits(username, habitName);
    }

    @DeleteMapping("/{habitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabit(@PathVariable Long habitId) {
        String username = CurrentUser.getCurrentUsername();
        habitService.deleteHabit(username, habitId);
    }

    // 6. upsertNote(noteDate, noteText) - user_id from JWT token
    @PostMapping("/upsertNote")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse upsertNote(@Valid @RequestBody NoteRequest req) {
        Note note = noteService.upsertNote(
                req.noteDate(),
                req.noteText()
        );
        return new NoteResponse(note);
    }

    @GetMapping("/notes")
    public List<NoteResponse> getNotes() {
        return noteService.getAllNotes();
    }
} 