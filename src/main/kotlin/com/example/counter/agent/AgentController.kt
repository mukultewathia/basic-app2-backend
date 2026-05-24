package com.example.counter.agent

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/agent")
class AgentController(private val agentService: AgentService) {

    private val logger = LoggerFactory.getLogger(AgentController::class.java)

    @PostMapping("/ask")
    fun askAgent(
        @RequestBody request: AgentRequestDTO,
        @RequestParam(name = "no-cache", required = false, defaultValue = "true") shouldUseCache: Boolean
    ): AgentResponseDTO {
        logger.info("Received incoming request: {}, shouldUseCache: {}", request, shouldUseCache)
        return agentService.getResponse(request, shouldUseCache)
    }
}
