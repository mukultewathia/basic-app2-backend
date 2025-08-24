package com.example.counter.weight;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class WeightDto {
    public record WeightRequest(
            @NotNull @DecimalMin("0.01") BigDecimal weightKg,
            Instant date) {}

    public record WeightResponse(
            Long id, Long userId, BigDecimal weightKg, Instant weighedAt) {
        public WeightResponse(WeightMetric e) {
            this(e.getMetricId(), e.getUser().getUserId(),
                    e.getWeightKgs(), e.getWeighedAt());
        }
    }

    public record DailyWeightAvg(
            LocalDate date,
            double avgWeightKg) {}

    public record AllWeightData(Long metricId, BigDecimal weightKgs, Instant weighedAt){}
}