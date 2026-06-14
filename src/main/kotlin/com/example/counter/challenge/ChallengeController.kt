package com.example.counter.challenge

import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import com.example.counter.challenge.ChallengeDto.*
import com.example.counter.notes.NoteDto.NoteRequest
import com.example.counter.notes.NoteDto.NoteResponse
import com.example.counter.notes.NoteService
import com.example.counter.auth.security.CurrentUser

@RestController
@RequestMapping("/api/challenge")
@CrossOrigin(origins = ["*"])
class ChallengeController(
    private val challengeService: ChallengeService,
    private val noteService: NoteService
) {

    // GET /api/challenge?status=...
    @GetMapping
    fun getChallenges(
        @RequestParam(name = "status", required = false) status: ChallengeStatus?
    ): List<ChallengeSummaryResponse> {
        val username = CurrentUser.getCurrentUsername()
        return challengeService.getChallengesByStatus(username, status)
    }

    // POST /api/challenge/create
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun createChallenge(@Valid @RequestBody request: ChallengeCreateRequest): ChallengeResponse {
        val username = CurrentUser.getCurrentUsername()
        println("creating challenge for username: $username")
        return challengeService.createChallenge(username, request)
    }

    // GET /api/challenge/{challengeId}
    @GetMapping("/{challengeId}")
    fun getChallengeDetails(@PathVariable challengeId: Long): ChallengeDetailResponse {
        val username = CurrentUser.getCurrentUsername()
        return challengeService.getChallengeDetails(username, challengeId)
    }

    // PATCH /api/challenge/{challengeId}
    @PatchMapping("/{challengeId}", consumes = ["application/merge-patch+json"])
    fun updateChallenge(
        @PathVariable challengeId: Long,
        @Valid @RequestBody request: ChallengeUpdateRequest
    ): ChallengeResponse {
        val username = CurrentUser.getCurrentUsername()
        return challengeService.updateChallenge(username, challengeId, request)
    }

    // DELETE /api/challenge/{challengeId}
    @DeleteMapping("/{challengeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteChallenge(@PathVariable challengeId: Long) {
        val username = CurrentUser.getCurrentUsername()
        challengeService.deleteChallenge(username, challengeId)
    }

    // PUT /api/challenge/{challengeId}/addHabit/{habitId}
    @PutMapping("/{challengeId}/addHabit/{habitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addHabitToChallenge(
        @PathVariable challengeId: Long,
        @PathVariable habitId: Long
    ) {
        val username = CurrentUser.getCurrentUsername()
        challengeService.addHabitToChallenge(username, challengeId, habitId)
    }

    // DELETE /api/challenge/{challengeId}/deleteHabit/{habitId}
    @DeleteMapping("/{challengeId}/deleteHabit/{habitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteHabitFromChallenge(
        @PathVariable challengeId: Long,
        @PathVariable habitId: Long
    ) {
        val username = CurrentUser.getCurrentUsername()
        challengeService.deleteHabitFromChallenge(username, challengeId, habitId)
    }

    // POST /api/challenge/{challengeId}/note
    @PostMapping("/{challengeId}/note")
    @ResponseStatus(HttpStatus.CREATED)
    fun upsertNoteToChallenge(
        @PathVariable challengeId: Long,
        @Valid @RequestBody request: NoteRequest
    ): NoteResponse {
        val username = CurrentUser.getCurrentUsername()
        return challengeService.upsertNoteToChallenge(username, challengeId, request.noteDate(), request.noteText())
    }

    // GET /api/challenge/{challengeId}/notes
    @GetMapping("/{challengeId}/notes")
    fun getNotesByChallenge(@PathVariable challengeId: Long): List<NoteResponse> {
        val username = CurrentUser.getCurrentUsername()
        return noteService.getNotesByChallenge(username, challengeId)
    }
}
