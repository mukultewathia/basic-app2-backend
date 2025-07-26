package com.example.counter.weight;

import com.example.counter.user.User;
import com.example.counter.user.UserRepository;
import com.example.counter.weight.WeightDto.DailyWeightAvg;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service 
public class WeightMetricService {
    private final WeightMetricRepository repo;
    private final UserRepository userRepo;

    public WeightMetricService(WeightMetricRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }


    // Adds a weight metric to the database.
    public WeightMetric add(String userName, BigDecimal weightKgs, Instant weighedAt) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Fetch the user from the database
        User user = userRepo.findByUsername(userName).orElseThrow();

        // Create the weight metric
        WeightMetric metric = new WeightMetric(user, weightKgs, weighedAt != null ? weighedAt : Instant.now());

        // Save the weight metric to the database.
        WeightMetric saved = repo.save(metric);

        stopWatch.stop();
        logTime("add weight metric took ms = ", stopWatch);
        return saved;
    }


    // Deletes a weight metric from the database by its id.
    public void delete(Long metricId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        repo.deleteById(metricId);

        stopWatch.stop();
        logTime("delete weight metric took ms = ", stopWatch);
    }

    // Returns daily average weight for a user.
    public List<DailyWeightAvg> getDailyAverages(String userName) {
        System.out.println("get daily averages for user " + userName);
        Long userId = userRepo.findByUsername(userName).orElseThrow().getUserId();
        return repo.findDailyAverages(userId).stream().map(p -> new DailyWeightAvg(
                p.getDate(),
                p.getAvgWeightKg()))
                .toList();
    }

    private void logTime(String message, StopWatch stopWatch) {
        System.out.println(message + stopWatch.getTotalTimeMillis() + " ms");
    }
}
