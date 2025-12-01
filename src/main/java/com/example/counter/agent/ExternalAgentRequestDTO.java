package com.example.counter.agent;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalAgentRequestDTO {
    private String query;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("session_id")
    private String sessionId;

    public ExternalAgentRequestDTO() {}

    public ExternalAgentRequestDTO(String query, String userId, String sessionId) {
        this.query = query;
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    @Override
    public String toString() {
        return "ExternalAgentRequestDTO{" +
                "query='" + query + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
