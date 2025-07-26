package com.example.counter.weight;

import com.example.counter.metric.Metric;
import com.example.counter.user.User;
import com.example.counter.metric.MetricKind;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "weight_metrics")
public class WeightMetric extends Metric {
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal weightKgs;
    
    private Instant weighedAt;

    WeightMetric() {
        super();
    }

    WeightMetric(User user, BigDecimal weightKgs, Instant weighedAt) {
        super(user, MetricKind.WEIGHT);
        this.weightKgs = weightKgs;
        this.weighedAt = weighedAt;
    }

    public void setWeightKgs(BigDecimal weightKgs) {
        this.weightKgs = weightKgs;
    }

    public BigDecimal getWeightKgs() {
        return weightKgs;
    }

    public Instant getWeighedAt() {
        return weighedAt;
    }

    public void setWeighedAt(Instant weighedAt) {
        this.weighedAt = weighedAt;
    }
}
