package com.example.counter.habit;

import com.example.counter.user.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
public class Habit {
    @Id
    @Column(name = "habit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long habitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitEntry> habitEntries = new ArrayList<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.example.counter.challenge.ChallengeHabits> challengeHabits = new ArrayList<>();

    // Constructors
    public Habit() {
        this.createdAt = getIndiaTimestamp();
    }

    public Habit(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.createdAt = getIndiaTimestamp();
    }
    
    // Helper method to get current timestamp in India timezone
    private OffsetDateTime getIndiaTimestamp() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return OffsetDateTime.now(indiaZone);
    }

    // Getters and setters
    public Long getHabitId() {
        return habitId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<HabitEntry> getHabitEntries() {
        return habitEntries;
    }

    public void setHabitEntries(List<HabitEntry> habitEntries) {
        this.habitEntries = habitEntries;
    }

    public List<com.example.counter.challenge.ChallengeHabits> getChallengeHabits() {
        return challengeHabits;
    }

    public void setChallengeHabits(List<com.example.counter.challenge.ChallengeHabits> challengeHabits) {
        this.challengeHabits = challengeHabits;
    }
} 