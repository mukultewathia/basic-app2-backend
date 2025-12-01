package com.example.counter.agent;

public class AgentRequestDTO {
    private String userQuery;

    public AgentRequestDTO() {}

    public AgentRequestDTO(String userQuery) {
        this.userQuery = userQuery;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }
    @Override
    public String toString() {
        return "AgentRequestDTO{" +
                "userQuery='" + userQuery + '\'' +
                '}';
    }
}
