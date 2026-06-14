package com.example.counter.challenge

import com.example.counter.habit.Habit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChallengeHabitsRepository : JpaRepository<ChallengeHabits, ChallengeHabitsId> {
    
    @Query("SELECT ch FROM ChallengeHabits ch WHERE ch.challengeId = :challengeId")
    fun findByChallengeId(@Param("challengeId") challengeId: Long): List<ChallengeHabits>
    
    @Query("SELECT ch FROM ChallengeHabits ch WHERE ch.habitId = :habitId")
    fun findByHabitId(@Param("habitId") habitId: Long): List<ChallengeHabits>
    
    @Query("SELECT ch FROM ChallengeHabits ch WHERE ch.challengeId = :challengeId AND ch.habitId = :habitId")
    fun findByChallengeIdAndHabitId(@Param("challengeId") challengeId: Long, @Param("habitId") habitId: Long): ChallengeHabits?
    
    @Query(value = "SELECT h.* FROM habits h JOIN challenge_habits ch ON h.habit_id = ch.habit_id WHERE ch.challenge_id = :challengeId", nativeQuery = true)
    fun findHabitsByChallengeId(@Param("challengeId") challengeId: Long): List<Habit>
    
    fun deleteByChallengeIdAndHabitId(challengeId: Long, habitId: Long)
}
