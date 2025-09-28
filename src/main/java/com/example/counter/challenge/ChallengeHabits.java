package com.example.counter.challenge;

import com.example.counter.habit.Habit;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "challenge_habits")
@IdClass(ChallengeHabitsId.class)
public class ChallengeHabits {
    @Id
    @Column(name = "challenge_id")
    private Long challengeId;

    @Id
    @Column(name = "habit_id")
    private Long habitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", insertable = false, updatable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", insertable = false, updatable = false)
    private Habit habit;

    @Column(name = "added_at", nullable = false)
    private OffsetDateTime addedAt;

    // Constructors
    public ChallengeHabits() {
        this.addedAt = getIndiaTimestamp();
    }

    public ChallengeHabits(Long challengeId, Long habitId) {
        this.challengeId = challengeId;
        this.habitId = habitId;
        this.addedAt = getIndiaTimestamp();
    }

    public ChallengeHabits(Challenge challenge, Habit habit) {
        this.challengeId = challenge.getChallengeId();
        this.habitId = habit.getHabitId();
        this.challenge = challenge;
        this.habit = habit;
        this.addedAt = getIndiaTimestamp();
    }
    
    // Helper method to get current timestamp in India timezone
    private OffsetDateTime getIndiaTimestamp() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        return OffsetDateTime.now(indiaZone);
    }

    // Getters and setters
    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
        this.challengeId = challenge.getChallengeId();
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
        this.habitId = habit.getHabitId();
    }

    public OffsetDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(OffsetDateTime addedAt) {
        this.addedAt = addedAt;
    }
}



