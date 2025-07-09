package com.example.counter;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(
    name   = "weight_log",
    schema = "health",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "weighed_at"})
)
public class WeightLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "weighed_at", nullable = false)
    private Instant weighedAt = Instant.now();

    @Column(name = "weight_kg", nullable = false, precision = 8, scale = 2)
    @DecimalMin(value = "0.01") 
    private BigDecimal weightKg;

    public Long getId()                 { return id; }
    public Long getUserId()             { return userId; }
    public Instant getWeighedAt()       { return weighedAt; }
    public BigDecimal getWeightKg()     { return weightKg; }

    public void setId(Long id)                        { this.id = id; }
    public void setUserId(Long userId)                { this.userId = userId; }
    public void setWeighedAt(Instant weighedAt)       { this.weighedAt = weighedAt; }
    public void setWeightKg(BigDecimal weightKg)      { this.weightKg = weightKg; }
}
