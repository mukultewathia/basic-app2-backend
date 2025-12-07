package com.example.counter.agent;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AgentController.class);

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/ask")
    public AgentResponseDTO askAgent(
            @RequestBody AgentRequestDTO request,
            @RequestParam(name = "no-cache", required = false, defaultValue = "false") boolean noCache) {

        logger.info("Received incoming request: {}, no-cache: {}", request, noCache);

        return agentService.getResponse(request, noCache);
    }
}
