package com.example.counter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;  

public interface WeightMetricRepository extends JpaRepository<WeightMetric, Long> {
    // extra read/query methods can go here later
    List<WeightMetric> findAllByMetricId(Long metricId);

     /**
     * Uses PostgreSQL DATE() cast + AVG().
     */
    @Query(value = """
        SELECT DATE(wm.creation_timestamp)          AS date,
               ROUND(AVG(wm.weight_kgs), 2)  AS avg_weight_kg
        FROM   weight_metrics wm
        JOIN   metrics m ON wm.metric_id = m.metric_id
        WHERE  m.user_id = :userId
        GROUP  BY DATE(wm.creation_timestamp)
        ORDER  BY DATE(wm.creation_timestamp)
        """,
        nativeQuery = true)
    List<DailyAvgProjection> findDailyAverages(@Param("userId") Long userId);

    /** Interface-based projection ↔ record constructor */
    interface DailyAvgProjection {
        LocalDate getDate();
        Double    getAvgWeightKg();
    }
}