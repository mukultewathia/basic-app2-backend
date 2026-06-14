package com.example.counter.challenge

enum class ChallengeStatus {
    scheduled,  // Challenge is scheduled for future
    active,     // Challenge is currently running
    expired,    // Challenge has ended
    deleted     // Challenge has been soft deleted
}
