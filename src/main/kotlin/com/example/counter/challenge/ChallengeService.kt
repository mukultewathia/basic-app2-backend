package com.example.counter.challenge

import com.example.counter.user.User
import com.example.counter.user.UserRepository
import com.example.counter.habit.Habit
import com.example.counter.habit.HabitEntry
import com.example.counter.habit.HabitRepository
import com.example.counter.habit.HabitEntryRepository
import com.example.counter.notes.Note
import com.example.counter.notes.NoteRepository
import com.example.counter.notes.NoteDto.NoteResponse
import com.example.counter.challenge.ChallengeDto.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StopWatch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Optional
import java.util.function.Supplier

@Service
class ChallengeService(
    private val challengeRepo: ChallengeRepository,
    private val challengeHabitsRepo: ChallengeHabitsRepository,
    private val habitRepo: HabitRepository,
    private val habitEntryRepo: HabitEntryRepository,
    private val userRepo: UserRepository,
    private val noteRepo: NoteRepository
) {

    @Transactional(rollbackFor = [Exception::class])
    fun createChallenge(username: String, request: ChallengeCreateRequest): ChallengeResponse {
        return executeWithTiming("create challenge") {
            println("createChallengeRequest: $request")

            val user = validateAndGetUser(username)
            validateChallengeUniqueness(username, request.name, request.startDate)
            val habits = validateAndGetHabits(username, request.habitIds)

            // Create challenge
            var challenge = Challenge(user, request.name, request.startDate, request.durationDays)
            challenge = challengeRepo.save(challenge)

            // Add habits to challenge
            for (habit in habits) {
                val challengeHabit = ChallengeHabits(challenge, habit)
                challengeHabitsRepo.save(challengeHabit)
            }

            ChallengeResponse(challenge)
        }
    }

    @Transactional
    fun getChallengesByStatus(username: String, status: ChallengeStatus?): List<ChallengeSummaryResponse> {
        return executeWithTiming("get challenges by status") {
            val challenges = if (status != null) {
                challengeRepo.findByUsernameAndStatus(username, status)
            } else {
                challengeRepo.findByUsernameAndNotDeleted(username)
            }

            // Update status of scheduled and active challenges based on current date
            updateChallengeStatuses(challenges)

            challenges.map { ChallengeSummaryResponse(it) }
        }
    }

    fun getChallengeDetails(username: String, challengeId: Long): ChallengeDetailResponse {
        return executeWithTiming("get challenge details") {
            val challenge = validateAndGetChallenge(username, challengeId)
            val habits = challengeHabitsRepo.findHabitsByChallengeId(challengeId)
            val habitsInfo = processHabitEntriesForChallenge(challenge, habits)
            
            ChallengeDetailResponse(challenge, habitsInfo)
        }
    }

    @Transactional
    fun updateChallenge(username: String, challengeId: Long, request: ChallengeUpdateRequest): ChallengeResponse {
        return executeWithTiming("update challenge") {
            var challenge = validateAndGetChallenge(username, challengeId)
            var updated = false

            if (!request.name.isNullOrBlank()) {
                challenge.name = request.name
                updated = true
            }

            if (request.startDate != null) {
                challenge.startDate = request.startDate
                updated = true
            }

            if (request.durationDays != null) {
                challenge.durationDays = request.durationDays
                updated = true
            }

            if (updated) {
                challenge = challengeRepo.save(challenge)
            }

            ChallengeResponse(challenge)
        }
    }

    @Transactional
    fun deleteChallenge(username: String, challengeId: Long) {
        executeWithTiming("delete challenge") {
            val challenge = validateAndGetChallenge(username, challengeId)
            // Soft delete by setting status to deleted
            challenge.scheduleStatus = ChallengeStatus.deleted
            challengeRepo.save(challenge)
        }
    }

    @Transactional
    fun addHabitToChallenge(username: String, challengeId: Long, habitId: Long) {
        executeWithTiming("add habit to challenge") {
            val challenge = validateAndGetChallenge(username, challengeId)
            val habit = validateAndGetHabit(username, habitId)

            // Check if habit is already in challenge
            val existing = challengeHabitsRepo.findByChallengeIdAndHabitId(challengeId, habitId)
            if (existing != null) {
                throw RuntimeException("Habit is already added to this challenge")
            }

            // Add habit to challenge
            val challengeHabit = ChallengeHabits(challenge, habit)
            challengeHabitsRepo.save(challengeHabit)
        }
    }

    @Transactional
    fun deleteHabitFromChallenge(username: String, challengeId: Long, habitId: Long) {
        executeWithTiming("delete habit from challenge") {
            validateAndGetChallenge(username, challengeId)
            validateAndGetHabit(username, habitId)

            // Check if habit is in challenge
            val existing = challengeHabitsRepo.findByChallengeIdAndHabitId(challengeId, habitId)
            if (existing == null) {
                throw RuntimeException("Habit is not in this challenge")
            }

            // Remove habit from challenge
            challengeHabitsRepo.deleteByChallengeIdAndHabitId(challengeId, habitId)
        }
    }

    @Transactional
    fun upsertNoteToChallenge(username: String, challengeId: Long, noteDate: LocalDate, noteText: String): NoteResponse {
        return executeWithTiming("upsert note to challenge") {
            val challenge = validateAndGetChallenge(username, challengeId)
            val user = validateAndGetUser(username)
            
            // Check if note exists for this date without challenge
            val existingNote = noteRepo.findByUserAndNoteDate(user, noteDate)
            if (existingNote.isPresent) {
                updateExistingNote(existingNote.get(), noteText, challenge)
            } else {
                createNewNote(user, noteText, challenge, noteDate)
            }
        }
    }

    // ==================== HELPER METHODS ====================
    
    private fun updateExistingNote(note: Note, noteText: String, challenge: Challenge): NoteResponse {
        val message: String
        if (note.challenge == null) {
            note.challenge = challenge
            message = "Note already existed and has been attached to the challenge"
        } else {
            note.noteText = noteText
            message = "Note updated successfully"
        }
        noteRepo.save(note)
        return NoteResponse(note, message)
    }

    private fun createNewNote(user: User, noteText: String, challenge: Challenge, noteDate: LocalDate): NoteResponse {
        var newNote = Note(user, noteText, noteDate, challenge)
        newNote = noteRepo.save(newNote)
        return NoteResponse(newNote, "Note created successfully")
    }

    private fun validateAndGetUser(username: String): User {
        return userRepo.findByUsername(username)
            .orElseThrow { RuntimeException("User not found: $username") }
    }
    
    private fun validateAndGetChallenge(username: String, challengeId: Long): Challenge {
        return challengeRepo.findByUsernameAndChallengeIdAndNotDeleted(username, challengeId)
            .orElseThrow { RuntimeException("Challenge not found with id: $challengeId") }
    }
    
    private fun validateAndGetHabit(username: String, habitId: Long): Habit {
        val habit = habitRepo.findById(habitId)
            .orElseThrow { RuntimeException("Habit not found with id: $habitId") }
        
        if (habit.user.username != username) {
            throw RuntimeException("Habit does not belong to user: $username")
        }
        
        return habit
    }
    
    private fun validateAndGetHabits(username: String, habitIds: List<Long>): List<Habit> {
        val habits = habitRepo.findHabitsByIdsAndUsername(habitIds, username)
        if (habits.size != habitIds.size) {
            throw RuntimeException("One or more habit IDs do not exist or do not belong to user: $username")
        }
        return habits
    }
    
    private fun validateChallengeUniqueness(username: String, name: String, startDate: LocalDate) {
        val existingChallenge = challengeRepo.findByUsernameAndNameAndStartDateAndNotDeleted(
            username, name, startDate
        )
        if (existingChallenge.isPresent) {
            throw RuntimeException("Challenge with name '$name' and start date '$startDate' already exists for user: $username")
        }
    }
    
    private fun processHabitEntriesForChallenge(challenge: Challenge, habits: List<Habit>): List<HabitInfo> {
        val habitIds = habits.mapNotNull { it.habitId }
        val allEntries = habitEntryRepo.findByHabitIds(habitIds)
        
        val challengeEntries = allEntries.filter { entry ->
            !entry.entryDate.isBefore(challenge.startDate) && 
            (challenge.endDate == null || !entry.entryDate.isAfter(challenge.endDate))
        }

        return habits.map { habit ->
            val habitEntries = challengeEntries.filter { entry ->
                entry.habit.habitId == habit.habitId
            }
            HabitInfo(habit, habitEntries)
        }
    }
    
    private fun <T> executeWithTiming(operationName: String, operation: () -> T): T {
        val stopWatch = StopWatch()
        stopWatch.start()
        
        try {
            return operation()
        } finally {
            stopWatch.stop()
            logTime("$operationName took ms = ", stopWatch)
        }
    }
    
    private fun updateChallengeStatuses(challenges: List<Challenge>) {
        val indiaZone = ZoneId.of("Asia/Kolkata")
        val today = LocalDate.now(indiaZone)
        
        var hasUpdates = false
        
        for (challenge in challenges) {
            if (challenge.scheduleStatus == ChallengeStatus.scheduled || 
                challenge.scheduleStatus == ChallengeStatus.active) {
                
                val newStatus = determineChallengeStatus(challenge, today)
                
                if (challenge.scheduleStatus != newStatus) {
                    challenge.scheduleStatus = newStatus
                    hasUpdates = true
                    println("Updated challenge '${challenge.name}' status from ${challenge.scheduleStatus} to $newStatus")
                }
            }
        }
        
        if (hasUpdates) {
            challengeRepo.saveAll(challenges)
        }
    }
    
    private fun determineChallengeStatus(challenge: Challenge, today: LocalDate): ChallengeStatus {
        val startDate = challenge.startDate
        val endDate = challenge.endDate
        
        return if (startDate.isAfter(today)) {
            ChallengeStatus.scheduled
        } else if (startDate.isEqual(today) || (startDate.isBefore(today) && (endDate == null || endDate.isAfter(today))) || endDate?.isEqual(today) == true) {
            ChallengeStatus.active
        } else {
            ChallengeStatus.expired
        }
    }

    private fun logTime(message: String, stopWatch: StopWatch) {
        println(message + stopWatch.totalTimeMillis + " ms")
    }
}
