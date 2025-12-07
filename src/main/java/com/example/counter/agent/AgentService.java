package com.example.counter.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentService {

    private static final Logger logger = LoggerFactory.getLogger(AgentService.class);

    private final RestClient restClient;
    private final String agentServiceUrl;
    private final Map<String, Map<String, CacheEntry>> responseCache = new ConcurrentHashMap<>();

    private record CacheEntry(LocalDate date, String response) {}

    public AgentService(@Value("${agent.service.url}") String agentServiceUrl) {
        this.restClient = RestClient.create();
        this.agentServiceUrl = agentServiceUrl;
    }

    public AgentResponseDTO getResponse(AgentRequestDTO request, boolean noCache) {
        Long userIdLong = com.example.counter.auth.security.CurrentUser.getCurrentUserId();
        String userId = userIdLong != null ? String.valueOf(userIdLong) : "anonymous";

        AgentResponseDTO errorResponse = sessionIdentifierError(request);
        if (errorResponse != null) {
            return errorResponse;
        }

        String cacheKey = getSessionId(request);

        if (!noCache) {
            AgentResponseDTO cachedResponse = getCachedResponse(userId, cacheKey);
            if (cachedResponse != null) {
                return cachedResponse;
            }
        }
        return fetchAndCacheResponse(userId, cacheKey, request);

    }

    private AgentResponseDTO sessionIdentifierError(AgentRequestDTO request) {
        int contextCount = 0;
        if (request.getChallengeId() != null) contextCount++;
        if (request.getHabitId() != null) contextCount++;
        if (request.getChatId() != null) contextCount++;


        if (contextCount > 1) {
            return new AgentResponseDTO("Error: Only one of challengeId, habitId, or chatId can be provided.");
        }

        if(contextCount == 0) {
            return new AgentResponseDTO("Error: At least one of challengeId, habitId, or chatId must be provided.");
        }

        return null;
    }


    private String getSessionId(AgentRequestDTO request) {
        if (request.getChallengeId() != null) {
            return "challengeId:" + request.getChallengeId();
        } else if (request.getHabitId() != null) {
            return "habitId:" + request.getHabitId();
        } else if (request.getChatId() != null) {
            return "chatId:" + request.getChatId();
        } else {
            // Fallback to userQuery if no context is provided
            return "query:" + request.getUserQuery();
        }
    }

    private AgentResponseDTO getCachedResponse(String userId, String cacheKey) {
        LocalDate today = LocalDate.now();
        Map<String, CacheEntry> userCache = responseCache.get(userId);
        if (userCache == null) return null;

        CacheEntry entry = userCache.get(cacheKey);
        if (entry != null && entry.date().equals(today)) {
            logger.info("Returning cached response for user: {}, key: {}", userId, cacheKey);
            return new AgentResponseDTO(entry.response());
        }

        return null;
    }

    private AgentResponseDTO fetchAndCacheResponse(String userId, String cacheKey, AgentRequestDTO request) {
        ExternalAgentRequestDTO externalRequest = new ExternalAgentRequestDTO(
                request.getUserQuery(),
                userId,
                getSessionId(request)
        );

        logger.info("Preparing to send request to Agent Service at: {}", agentServiceUrl);
        logger.info("Outgoing request payload: {}", externalRequest);

        try {
            AgentResponseDTO response = restClient.post()
                    .uri(agentServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(externalRequest)
                    .retrieve()
                    .body(AgentResponseDTO.class);

            logger.info("Received response from Agent Service: {}", response);

            if (response != null && response.getResponse() != null) {
                cacheResponse(userId, cacheKey, response.getResponse());
            }

            return response;
        } catch (Exception e) {
            logger.error("Error calling Agent Service", e);
            return new AgentResponseDTO("Error: " + e.getMessage());
        }
    }

    private void cacheResponse(String userId, String cacheKey, String responseContent) {
        responseCache.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(cacheKey, new CacheEntry(LocalDate.now(), responseContent));
    }
}
