package com.example.counter.agent;

public class AgentRequestDTO {
    private String userQuery;

    private Integer challengeId;
    private Long habitId;
    private String chatId;

    public AgentRequestDTO() {}

    public AgentRequestDTO(String userQuery, Integer challengeId, Long habitId, String chatId) {
        this.userQuery = userQuery;
        this.challengeId = challengeId;
        this.habitId = habitId;
        this.chatId = chatId;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "AgentRequestDTO{" +
                "userQuery='" + userQuery + '\'' +
                ", challengeId=" + challengeId +
                ", habitId=" + habitId +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
