package com.example.counter.metric;

import com.example.counter.user.User;
import com.example.counter.metric.MetricKind;
import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "metrics")
public abstract class Metric {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long metricId;

  @ManyToOne(fetch = FetchType.LAZY)        // user_id FK
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "metric_type", nullable = false)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM) 
  private MetricKind kind;


  private Instant creationTimestamp;

  public Metric() {}

  public Metric(User user, MetricKind kind) {
    this.user = user;
    this.kind = kind;
    this.creationTimestamp = Instant.now();
  }

  public Long getMetricId() {
    return metricId;
  }

  public User getUser() {
    return user;
  }

  public MetricKind getKind() {
    return kind;
  }

  public Instant getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setKind(MetricKind kind) {
    this.kind = kind;
  }

  public void setCreationTimestamp(Instant creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }
}