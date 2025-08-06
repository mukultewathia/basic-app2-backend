package com.example.counter.habit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    
    @Query("SELECT h FROM Habit h WHERE h.user.username = :username")
    List<Habit> findByUsername(@Param("username") String username);
    
    @Query("SELECT h FROM Habit h WHERE h.user.username = :username AND h.name = :habitName")
    Optional<Habit> findByUsernameAndName(@Param("username") String username, @Param("habitName") String habitName);
    
    @Query("SELECT h FROM Habit h WHERE h.user.username = :username AND h.name IN :habitNames")
    List<Habit> findByUsernameAndNameIn(@Param("username") String username, @Param("habitNames") List<String> habitNames);
} 