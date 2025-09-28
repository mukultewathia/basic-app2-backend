package com.example.counter.challenge;

import com.example.counter.habit.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChallengeHabitsRepository extends JpaRepository<ChallengeHabits, ChallengeHabitsId> {
    
    @Query("SELECT ch FROM ChallengeHabits ch WHERE ch.challengeId = :challengeId")
    List<ChallengeHabits> findByChallengeId(@Param("challengeId") Long challengeId);
    
    @Query("SELECT ch FROM ChallengeHabits ch WHERE ch.habitId = :habitId")
    List<ChallengeHabits> findByHabitId(@Param("habitId") Long habitId);
    
    @Query("SELECT ch FROM ChallengeHabits ch WHERE ch.challengeId = :challengeId AND ch.habitId = :habitId")
    Optional<ChallengeHabits> findByChallengeIdAndHabitId(@Param("challengeId") Long challengeId, @Param("habitId") Long habitId);
    
    @Query(value = "SELECT h.* FROM habits h JOIN challenge_habits ch ON h.habit_id = ch.habit_id WHERE ch.challenge_id = :challengeId", nativeQuery = true)
    List<Habit> findHabitsByChallengeId(@Param("challengeId") Long challengeId);
    
    void deleteByChallengeIdAndHabitId(Long challengeId, Long habitId);
}



