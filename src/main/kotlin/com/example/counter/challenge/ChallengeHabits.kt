package com.example.counter.challenge

import com.example.counter.habit.Habit
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.time.ZoneId

@Entity
@Table(name = "challenge_habits")
@IdClass(ChallengeHabitsId::class)
class ChallengeHabits {
    @Id
    @Column(name = "challenge_id")
    var challengeId: Long? = null

    @Id
    @Column(name = "habit_id")
    var habitId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", insertable = false, updatable = false)
    var challenge: Challenge? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", insertable = false, updatable = false)
    var habit: Habit? = null

    @Column(name = "added_at", nullable = false)
    var addedAt: OffsetDateTime = getIndiaTimestamp()

    constructor()

    constructor(challengeId: Long, habitId: Long) {
        this.challengeId = challengeId
        this.habitId = habitId
        this.addedAt = getIndiaTimestamp()
    }

    constructor(challenge: Challenge, habit: Habit) {
        this.challenge = challenge
        this.habit = habit
        this.challengeId = challenge.challengeId
        this.habitId = habit.habitId
        this.addedAt = getIndiaTimestamp()
    }

    private fun getIndiaTimestamp(): OffsetDateTime {
        val indiaZone = ZoneId.of("Asia/Kolkata")
        return OffsetDateTime.now(indiaZone)
    }
}
