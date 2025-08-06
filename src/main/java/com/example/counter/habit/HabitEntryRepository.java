package com.example.counter.habit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitEntryRepository extends JpaRepository<HabitEntry, Long> {
    
    @Query("SELECT he FROM HabitEntry he WHERE he.habit.habitId IN :habitIds")
    List<HabitEntry> findByHabitIds(@Param("habitIds") List<Long> habitIds);
    
    @Query("SELECT he FROM HabitEntry he WHERE he.habit.user.username = :username AND he.habit.name IN :habitNames")
    List<HabitEntry> findByUsernameAndHabitNames(@Param("username") String username, @Param("habitNames") List<String> habitNames);
    
    @Query(value = """
        SELECT he.* FROM habit_entries he 
        JOIN habits h ON he.habit_id = h.habit_id 
        JOIN users u ON h.user_id = u.user_id 
        WHERE u.username = :username 
        AND h.name = :habitName 
        AND DATE(he.entry_date AT TIME ZONE 'Asia/Kolkata') = :entryDate
        """, nativeQuery = true)
    Optional<HabitEntry> findByUsernameAndHabitNameAndDate(@Param("username") String username, @Param("habitName") String habitName, @Param("entryDate") LocalDate entryDate);
} 