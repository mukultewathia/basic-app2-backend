package com.example.counter.challenge;

import com.example.counter.habit.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.scheduleStatus != 'deleted' ORDER BY c.createdAt DESC")
    List<Challenge> findByUsernameAndNotDeleted(@Param("username") String username);
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.scheduleStatus = :status ORDER BY c.createdAt DESC")
    List<Challenge> findByUsernameAndStatus(@Param("username") String username, @Param("status") ChallengeStatus status);
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.challengeId = :challengeId AND c.scheduleStatus != 'deleted'")
    Optional<Challenge> findByUsernameAndChallengeIdAndNotDeleted(@Param("username") String username, @Param("challengeId") Long challengeId);
    
    @Query("SELECT c FROM Challenge c WHERE c.user.username = :username AND c.name = :name AND c.startDate = :startDate AND c.scheduleStatus != 'deleted'")
    Optional<Challenge> findByUsernameAndNameAndStartDateAndNotDeleted(@Param("username") String username, @Param("name") String name, @Param("startDate") java.time.LocalDate startDate);
    
    @Query("SELECT h FROM Habit h WHERE h.habitId IN :habitIds AND h.user.username = :username")
    List<Habit> findHabitsByIdsAndUsername(@Param("habitIds") List<Long> habitIds, @Param("username") String username);
}



