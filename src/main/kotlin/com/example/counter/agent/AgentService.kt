package com.example.counter.agent

import com.example.counter.auth.security.CurrentUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

@Service
class AgentService(
    @Value("\${agent.service.url}") private val agentServiceUrl: String
) {
    private val logger = LoggerFactory.getLogger(AgentService::class.java)
    private val restClient = RestClient.create()
    private val responseCache = ConcurrentHashMap<String, ConcurrentHashMap<String, CacheEntry>>()

    private data class CacheEntry(val date: LocalDate, val response: String)

    fun getResponse(request: AgentRequestDTO, shouldUseCache: Boolean): AgentResponseDTO {
        val userIdLong = CurrentUser.getCurrentUserId()
        val userId = userIdLong?.toString() ?: "anonymous"

        val errorResponse = sessionIdentifierError(request)
        if (errorResponse != null) {
            return errorResponse
        }

        val cacheKey = getSessionId(request)

        if (shouldUseCache) {
            val cachedResponse = getCachedResponse(userId, cacheKey)
            if (cachedResponse != null) {
                return cachedResponse
            }
        }
        return fetchAndCacheResponse(userId, cacheKey, request)
    }

    private fun sessionIdentifierError(request: AgentRequestDTO): AgentResponseDTO? {
        var contextCount = 0
        if (request.challengeId != null) contextCount++
        if (request.habitId != null) contextCount++
        if (request.chatId != null) contextCount++

        if (contextCount > 1) {
            return AgentResponseDTO("Error: Only one of challengeId, habitId, or chatId can be provided.")
        }

        if (contextCount == 0) {
            return AgentResponseDTO("Error: At least one of challengeId, habitId, or chatId must be provided.")
        }

        return null
    }

    private fun getSessionId(request: AgentRequestDTO): String {
        return when {
            request.challengeId != null -> "challengeId:${request.challengeId}"
            request.habitId != null -> "habitId:${request.habitId}"
            request.chatId != null -> "chatId:${request.chatId}"
            else -> "query:${request.userQuery}"
        }
    }

    private fun getCachedResponse(userId: String, cacheKey: String): AgentResponseDTO? {
        val today = LocalDate.now()
        val userCache = responseCache[userId] ?: return null
        val entry = userCache[cacheKey]

        if (entry != null && entry.date == today) {
            logger.info("Returning cached response for user: {}, key: {}", userId, cacheKey)
            return AgentResponseDTO(entry.response)
        }

        return null
    }

    private fun fetchAndCacheResponse(userId: String, cacheKey: String, request: AgentRequestDTO): AgentResponseDTO {
        val externalRequest = ExternalAgentRequestDTO(
            request.userQuery,
            userId,
            getSessionId(request)
        )

        logger.info("Preparing to send request to Agent Service at: {}", agentServiceUrl)
        logger.info("Outgoing request payload: {}", externalRequest)

        return try {
            val response: AgentResponseDTO? = restClient.post()
                .uri(agentServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(externalRequest)
                .retrieve()
                .body<AgentResponseDTO>()

            logger.info("Received response from Agent Service: {}", response)

            val responseText = response?.response
            if (responseText != null) {
                cacheResponse(userId, cacheKey, responseText)
            }

            response ?: AgentResponseDTO("Error: Empty response from service")
        } catch (e: Exception) {
            logger.error("Error calling Agent Service", e)
            AgentResponseDTO("Error: ${e.message}")
        }
    }

    private fun cacheResponse(userId: String, cacheKey: String, responseContent: String) {
        responseCache.computeIfAbsent(userId) { ConcurrentHashMap() }[cacheKey] = CacheEntry(LocalDate.now(), responseContent)
    }
}
