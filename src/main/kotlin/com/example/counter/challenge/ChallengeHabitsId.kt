package com.example.counter.challenge

import java.io.Serializable

data class ChallengeHabitsId(
    var challengeId: Long? = null,
    var habitId: Long? = null
) : Serializable
