package com.example.counter.agent

data class AgentRequestDTO(
    var userQuery: String? = null,
    var challengeId: Int? = null,
    var habitId: Long? = null,
    var chatId: String? = null
)
