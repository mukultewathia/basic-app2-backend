package com.example.counter.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.counter.auth.security.CurrentUser;
import com.example.counter.notes.NoteDto.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {
    
    @Autowired
    private NoteService noteService;

    // GET /api/notes - Get all notes for current user
    @GetMapping
    public List<NoteResponse> getAllNotes() {
        return noteService.getAllNotes();
    }

    // GET /api/notes/challenge/{challengeId} - Get all notes for a specific challenge
    @GetMapping("/challenge/{challengeId}")
    public List<NoteResponse> getNotesByChallenge(@PathVariable Long challengeId) {
        String username = CurrentUser.getCurrentUsername();
        return noteService.getNotesByChallenge(username, challengeId);
    }

    // POST /api/notes - Create or update a note
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse createOrUpdateNote(@Valid @RequestBody NoteRequest request) {
        return noteService.upsertNote(request.noteDate(), request.noteText());
    }

    // GET /api/notes/{date} - Get note for a specific date
    @GetMapping("/{date}")
    public NoteResponse getNoteByDate(@PathVariable java.time.LocalDate date) {
        return noteService.getNoteByDate(date);
    }
}
