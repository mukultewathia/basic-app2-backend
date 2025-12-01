package com.example.counter.agent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AgentController.class);

    private final RestClient restClient;
    private final String agentServiceUrl;

    public AgentController(@Value("${agent.service.url}") String agentServiceUrl) {
        this.restClient = RestClient.create();
        this.agentServiceUrl = agentServiceUrl;
    }

    @PostMapping("/ask")
    public AgentResponseDTO askAgent(@RequestBody AgentRequestDTO request) {
        ExternalAgentRequestDTO externalRequest = new ExternalAgentRequestDTO(
                request.getUserQuery(),
                "user123", // Hardcoded as per requirements/example
                "session_id" // Hardcoded as per requirements/example
        );

        try {
            AgentResponseDTO response = restClient.post()
                    .uri(agentServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(externalRequest)
                    .retrieve()
                    .body(AgentResponseDTO.class);

            return response;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("Error calling Agent Service", e);
            return new AgentResponseDTO("Error: " + e.getMessage());
        }

    }
}
