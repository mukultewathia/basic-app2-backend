package com.example.counter.agent;

public class AgentResponseDTO {
    private String response;

    public AgentResponseDTO() {}

    public AgentResponseDTO(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    @Override
    public String toString() {
        return "AgentResponseDTO{" +
                "response='" + response + '\'' +
                '}';
    }
}
