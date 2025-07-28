package com.example.counter.weight;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;  
import com.example.counter.weight.WeightMetric;
import java.math.BigDecimal;


public interface WeightMetricRepository extends JpaRepository<WeightMetric, Long> {
    // extra read/query methods can go here later
    List<WeightMetric> findAllByMetricId(Long metricId);

     /**
     * Uses PostgreSQL DATE() cast + AVG().
     */
    @Query(value = """
        SELECT DATE(wm.weighed_at)          AS date,
               ROUND(AVG(wm.weight_kgs), 2)  AS avg_weight_kg
        FROM   weight_metrics wm
        JOIN   metrics m ON wm.metric_id = m.metric_id
        WHERE  m.user_id = :userId
        GROUP  BY DATE(wm.weighed_at)
        ORDER  BY DATE(wm.weighed_at)
        """,
        nativeQuery = true)
    List<DailyAvgProjection> findDailyAverages(@Param("userId") Long userId);

    /** Interface-based projection ↔ record constructor */
    interface DailyAvgProjection {
        LocalDate getDate();
        Double    getAvgWeightKg();
    }

    @Query(value = """
        SELECT wm.metric_id, wm.weight_kgs, wm.weighed_at
        FROM   weight_metrics wm
        JOIN   metrics m ON wm.metric_id = m.metric_id
        WHERE  m.user_id = :userId
        ORDER  BY wm.weighed_at
        """,
        nativeQuery = true)
    List<AllWeightData> findAllData(@Param("userId") Long userId);


       /** Interface-based projection ↔ record constructor */
    interface AllWeightData {
        Long getMetricId();
        BigDecimal getWeightKgs();
        Instant getWeighedAt();
    }
}