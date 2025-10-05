package com.example.counter.challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.counter.challenge.ChallengeDto.*;
import com.example.counter.notes.NoteDto.NoteRequest;
import com.example.counter.notes.NoteDto.NoteResponse;
import com.example.counter.notes.NoteService;
import com.example.counter.auth.security.CurrentUser;
import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@CrossOrigin(origins = "*")
public class ChallengeController {
    @Autowired
    private ChallengeService challengeService;
    
    @Autowired
    private NoteService noteService;

    // GET /challenge/?status=...
    @GetMapping
    public List<ChallengeSummaryResponse> getChallenges(
            @RequestParam(name = "status", required = false) ChallengeStatus status) {
        String username = CurrentUser.getCurrentUsername();
        return challengeService.getChallengesByStatus(username, status);
    }

    // POST /challenge/create
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ChallengeResponse createChallenge(@Valid @RequestBody ChallengeCreateRequest request) {
        System.out.println("creating challenge for username: ");
        String username = CurrentUser.getCurrentUsername();
        System.out.println("creating challenge for username: " + username);

        return challengeService.createChallenge(username, request);
    }

    // GET /challenge/{challengeId}
    @GetMapping("/{challengeId}")
    public ChallengeDetailResponse getChallengeDetails(@PathVariable Long challengeId) {
        String username = CurrentUser.getCurrentUsername();
        return challengeService.getChallengeDetails(username, challengeId);
    }

    // PATCH /challenge/{challengeId}
    @PatchMapping(value = "/{challengeId}", consumes = "application/merge-patch+json")
    public ChallengeResponse updateChallenge(
            @PathVariable Long challengeId,
            @Valid @RequestBody ChallengeUpdateRequest request) {
        String username = CurrentUser.getCurrentUsername();
        return challengeService.updateChallenge(username, challengeId, request);
    }

    // DELETE /challenge/{challengeId}
    @DeleteMapping("/{challengeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChallenge(@PathVariable Long challengeId) {
        String username = CurrentUser.getCurrentUsername();
        challengeService.deleteChallenge(username, challengeId);
    }

    // PUT /challenge/{challengeId}/addHabit/{habitId}
    @PutMapping("/{challengeId}/addHabit/{habitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addHabitToChallenge(
            @PathVariable Long challengeId,
            @PathVariable Long habitId) {
        String username = CurrentUser.getCurrentUsername();
        challengeService.addHabitToChallenge(username, challengeId, habitId);
    }

    // DELETE /challenge/{challengeId}/deleteHabit/{habitId}
    @DeleteMapping("/{challengeId}/deleteHabit/{habitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabitFromChallenge(
            @PathVariable Long challengeId,
            @PathVariable Long habitId) {
        String username = CurrentUser.getCurrentUsername();
        challengeService.deleteHabitFromChallenge(username, challengeId, habitId);
    }

    // POST /challenge/{challengeId}/note
    @PostMapping("/{challengeId}/note")
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse upsertNoteToChallenge(
            @PathVariable Long challengeId,
            @Valid @RequestBody NoteRequest request) {
        String username = CurrentUser.getCurrentUsername();
        return challengeService.upsertNoteToChallenge(username, challengeId, request.noteDate(), request.noteText());
    }

    // GET /challenge/{challengeId}/notes
    @GetMapping("/{challengeId}/notes")
    public List<NoteResponse> getNotesByChallenge(@PathVariable Long challengeId) {
        String username = CurrentUser.getCurrentUsername();
        return noteService.getNotesByChallenge(username, challengeId);
    }
}



