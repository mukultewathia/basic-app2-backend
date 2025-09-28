package com.example.counter.challenge;

import java.io.Serializable;
import java.util.Objects;

public class ChallengeHabitsId implements Serializable {
    private Long challengeId;
    private Long habitId;

    public ChallengeHabitsId() {}

    public ChallengeHabitsId(Long challengeId, Long habitId) {
        this.challengeId = challengeId;
        this.habitId = habitId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeHabitsId that = (ChallengeHabitsId) o;
        return Objects.equals(challengeId, that.challengeId) &&
               Objects.equals(habitId, that.habitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(challengeId, habitId);
    }
}



