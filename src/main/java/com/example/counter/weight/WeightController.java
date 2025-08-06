package com.example.counter.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.counter.weight.WeightDto.WeightRequest;
import com.example.counter.weight.WeightDto.WeightResponse;
import com.example.counter.weight.WeightDto.AllWeightData;
import com.example.counter.weight.WeightDto.DailyWeightAvg;
import java.util.List;
import com.example.counter.user.User;
import com.example.counter.user.UserRepository;

@RestController
@RequestMapping("/api/weights")
@CrossOrigin(origins = "*")
public class WeightController {
    @Autowired
    private WeightMetricService weightMetricService;

    @PostMapping("/addWeight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightResponse add(@Valid @RequestBody WeightRequest req) {
        WeightMetric entity = weightMetricService.add(
                req.userName(),
                req.weightKg(),
                req.date()
        );
        return new WeightResponse(entity);
    }

    @DeleteMapping("/deleteWeight")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeight(@RequestParam(name = "metricId") Long metricId) {
        weightMetricService.delete(metricId);
    }

    @GetMapping("/dailyAverages")
    public List<DailyWeightAvg> getDailyAverages(@RequestParam(name = "userName", required = true) String userName) {
        return weightMetricService.getDailyAverages(userName);
    }

    @GetMapping("/allData")
    public List<AllWeightData> getAllData(@RequestParam(name = "userName", required = true) String userName) {
        return weightMetricService.getAllData(userName);
    }

    @PostMapping("/deleteByMetricId")
    public void deleteByMetricId(@RequestParam(name = "metricId") Long metricId) {
        weightMetricService.deleteByMetricId(metricId);
    }
}
