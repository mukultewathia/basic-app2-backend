package com.example.counter;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

@Service 
public class WeightMetricService {
    private final WeightMetricRepository repo;
    private final UserRepository userRepo;

    public WeightMetricService(WeightMetricRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }


    public WeightMetric addWeightMetric(String userName, BigDecimal weightKgs) {
        User user = userRepo.findByUsername(userName).orElseThrow();
        WeightMetric metric = new WeightMetric();
        metric.setUser(user);
        metric.setWeightKgs(weightKgs);
        metric.setKind(MetricKind.WEIGHT);
        metric.setCreationTimestamp(Instant.now());
        return repo.save(metric);
    }
}
