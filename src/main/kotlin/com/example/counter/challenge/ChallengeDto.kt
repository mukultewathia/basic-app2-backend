package com.example.counter.challenge

import com.example.counter.habit.Habit
import com.example.counter.habit.HabitEntry
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate
import java.time.OffsetDateTime

class ChallengeDto {
    data class ChallengeCreateRequest(
        @field:NotNull @field:NotBlank val name: String,
        @field:NotNull val habitIds: List<Long>,
        @field:NotNull val startDate: LocalDate,
        @field:NotNull @field:Positive val durationDays: Int,
        val challengeDescription: String? = null
    )

    data class ChallengeUpdateRequest(
        val name: String? = null,
        val startDate: LocalDate? = null,
        @field:Min(1) val durationDays: Int? = null,
        val challengeDescription: String? = null,
        val retrospective: String? = null
    )

    data class ChallengeSummaryResponse(
        val challengeId: Long?,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val scheduleStatus: ChallengeStatus,
        val completionStatus: CompletionStatus
    ) {
        constructor(challenge: Challenge) : this(
            challengeId = challenge.challengeId,
            name = challenge.name,
            startDate = challenge.startDate,
            endDate = challenge.endDate,
            scheduleStatus = challenge.scheduleStatus,
            completionStatus = challenge.completion
        )
    }

    data class HabitEntryInfo(
        val entryId: Long?,
        val entryDate: LocalDate,
        val performed: Boolean?,
        val notes: String?
    ) {
        constructor(entry: HabitEntry) : this(
            entryId = entry.entryId,
            entryDate = entry.entryDate,
            performed = entry.performed,
            notes = entry.notes
        )
    }

    data class HabitInfo(
        val habitId: Long?,
        val habitName: String,
        val habitDescription: String?,
        val habitEntries: List<HabitEntryInfo>
    ) {
        constructor(habit: Habit, entries: List<HabitEntry>) : this(
            habitId = habit.habitId,
            habitName = habit.name,
            habitDescription = habit.description,
            habitEntries = entries.map { HabitEntryInfo(it) }
        )
    }

    data class ChallengeDetailResponse(
        val challengeId: Long?,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val durationDays: Int,
        val scheduleStatus: ChallengeStatus,
        val completionStatus: CompletionStatus,
        val successPercent: Int,
        val habitsInfo: List<HabitInfo>,
        val createdAt: OffsetDateTime,
        val updatedAt: OffsetDateTime,
        val challengeDescription: String?,
        val retrospective: String?
    ) {
        constructor(challenge: Challenge, habitsInfo: List<HabitInfo>) : this(
            challengeId = challenge.challengeId,
            name = challenge.name,
            startDate = challenge.startDate,
            endDate = challenge.endDate,
            durationDays = challenge.durationDays,
            scheduleStatus = challenge.scheduleStatus,
            completionStatus = challenge.completion,
            successPercent = challenge.successPercent,
            habitsInfo = habitsInfo,
            createdAt = challenge.createdAt,
            updatedAt = challenge.updatedAt,
            challengeDescription = challenge.challengeDescription,
            retrospective = challenge.retrospective
        )
    }

    data class ChallengeResponse(
        val challengeId: Long?,
        val userId: Long?,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate?,
        val durationDays: Int,
        val scheduleStatus: ChallengeStatus,
        val completionStatus: CompletionStatus,
        val successPercent: Int,
        val createdAt: OffsetDateTime,
        val updatedAt: OffsetDateTime,
        val challengeDescription: String?,
        val retrospective: String?
    ) {
        constructor(challenge: Challenge) : this(
            challengeId = challenge.challengeId,
            userId = challenge.user?.userId,
            name = challenge.name,
            startDate = challenge.startDate,
            endDate = challenge.endDate,
            durationDays = challenge.durationDays,
            scheduleStatus = challenge.scheduleStatus,
            completionStatus = challenge.completion,
            successPercent = challenge.successPercent,
            createdAt = challenge.createdAt,
            updatedAt = challenge.updatedAt,
            challengeDescription = challenge.challengeDescription,
            retrospective = challenge.retrospective
        )
    }
}
