package com.example.counter.agent

import com.fasterxml.jackson.annotation.JsonProperty

data class ExternalAgentRequestDTO(
    var query: String? = null,
    @field:JsonProperty("user_id")
    var userId: String? = null,
    @field:JsonProperty("session_id")
    var sessionId: String? = null
)
