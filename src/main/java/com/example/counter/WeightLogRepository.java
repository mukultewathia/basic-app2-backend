package com.example.counter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;  

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
    // extra read/query methods can go here later
    List<WeightLog> findAllByUserId(Long userId);

     /**
     * Uses PostgreSQL DATE() cast + AVG().
     */
    @Query(value = """
        SELECT DATE(weighed_at)          AS date,
               ROUND(AVG(weight_kg), 2)  AS avg_weight_kg
        FROM   health.weight_log
        WHERE  user_id = :userId
        GROUP  BY DATE(weighed_at)
        ORDER  BY DATE(weighed_at)
        """,
        nativeQuery = true)
    List<DailyAvgProjection> findDailyAverages(@Param("userId") Long userId);

    /** Interface-based projection ↔ record constructor */
    interface DailyAvgProjection {
        LocalDate getDate();
        Double    getAvgWeightKg();
    }
}