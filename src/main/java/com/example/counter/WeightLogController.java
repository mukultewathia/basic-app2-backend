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
    private WeightLogRepository repo;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/addWeight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightLogResponse add(@Valid @RequestBody WeightLogRequest req) {
        WeightLog entity = new WeightLog();
        entity.setUserId(getUserId(req.userName));
        entity.setWeightKg(req.weightKg());
        entity.setWeighedAt(
                req.weighedAt() != null ? req.weighedAt() : Instant.now());

        return new WeightLogResponse(repo.save(entity));
    }

    @GetMapping("/dailyAverages")
    public List<DailyWeightAvg> getDailyAverages(@RequestParam(name = "userName", required = true) String userName) {
        System.out.println("get daily averages for user " + userName);
        return repo.findDailyAverages(getUserId(userName)).stream().map(p -> new DailyWeightAvg(
                p.getDate(),
                p.getAvgWeightKg()))
                .toList();
    }

    private Long getUserId(String userName) {
        return userRepository.findByUsername(userName).orElseThrow().getId();
    }

    /* ===== Data Transfer Objects ===== */
    public record WeightLogRequest(
            @NotNull String userName,
            @NotNull @DecimalMin("0.01") BigDecimal weightKg,
            Instant weighedAt) {
    }

    public record WeightLogResponse(
            Long id, Long userId, BigDecimal weightKg, Instant weighedAt) {
        WeightLogResponse(WeightLog e) {
            this(e.getId(), e.getUserId(),
                    e.getWeightKg(), e.getWeighedAt());
        }
    }

    public record DailyWeightAvg(
            LocalDate date,
            double avgWeightKg) {
    }
}
