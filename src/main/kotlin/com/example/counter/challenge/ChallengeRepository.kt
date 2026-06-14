package com.example.counter.challenge

import com.example.counter.habit.Habit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.time.LocalDate

interface ChallengeRepository : JpaRepository<Challenge, Long> {
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.scheduleStatus != 'deleted' ORDER BY c.createdAt DESC")
    fun findByUsernameAndNotDeleted(@Param("username") username: String): List<Challenge>
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.scheduleStatus = :status ORDER BY c.createdAt DESC")
    fun findByUsernameAndStatus(@Param("username") username: String, @Param("status") status: ChallengeStatus): List<Challenge>
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.challengeId = :challengeId AND c.scheduleStatus != 'deleted'")
    fun findByUsernameAndChallengeIdAndNotDeleted(@Param("username") username: String, @Param("challengeId") challengeId: Long): Optional<Challenge>
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.name = :name AND c.startDate = :startDate AND c.scheduleStatus != 'deleted'")
    fun findByUsernameAndNameAndStartDateAndNotDeleted(
        @Param("username") username: String,
        @Param("name") name: String,
        @Param("startDate") startDate: LocalDate
    ): Optional<Challenge>
    
    @Query("SELECT h FROM Habit h WHERE h.habitId IN :habitIds AND h.user.username = :username")
    fun findHabitsByIdsAndUsername(@Param("habitIds") habitIds: List<Long>, @Param("username") username: String): List<Habit>
}
