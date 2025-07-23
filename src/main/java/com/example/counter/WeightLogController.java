package com.example.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/weights")
@CrossOrigin(origins = "*")
public class WeightLogController {
    @Autowired
    private WeightMetricRepository repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeightMetricService weightMetricService;

    @PostMapping("/addWeight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightLogResponse add(@Valid @RequestBody WeightLogRequest req) {
        WeightMetric entity = weightMetricService.addWeightMetric(
                req.userName(), 
                req.weightKg()
        );

        return new WeightLogResponse(entity);
    }

    @GetMapping("/dailyAverages")
    public List<DailyWeightAvg> getDailyAverages(@RequestParam(name = "userName", required = true) String userName) {
        System.out.println("get daily averages for user " + userName);
        Long userId = getUserId(userName);
        return repo.findDailyAverages(userId).stream().map(p -> new DailyWeightAvg(
                p.getDate(),
                p.getAvgWeightKg()))
                .toList();
    }

    private Long getUserId(String userName) {
        return userRepository.findByUsername(userName).orElseThrow().getUserId();
    }

    /* ===== Data Transfer Objects ===== */
    public record WeightLogRequest(
            @NotNull String userName,
            @NotNull @DecimalMin("0.01") BigDecimal weightKg,
            Instant weighedAt) {
    }

    public record WeightLogResponse(
            Long id, Long userId, BigDecimal weightKg, Instant weighedAt) {
        WeightLogResponse(WeightMetric e) {
            this(e.getMetricId(), e.getUser().getUserId(),
                    e.getWeightKgs(), e.getCreationTimestamp());
        }
    }

    public record DailyWeightAvg(
            LocalDate date,
            double avgWeightKg) {
    }
}
