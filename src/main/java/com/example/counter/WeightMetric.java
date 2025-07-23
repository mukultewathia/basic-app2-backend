package com.example.counter;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "weight_metrics")
public class WeightMetric extends Metric {
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal weightKgs;

    public void setWeightKgs(BigDecimal weightKgs) {
        this.weightKgs = weightKgs;
    }

    public BigDecimal getWeightKgs() {
        return weightKgs;
    }
}
