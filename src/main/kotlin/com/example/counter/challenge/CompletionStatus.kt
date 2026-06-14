package com.example.counter.challenge

enum class CompletionStatus {
    not_started,      // Challenge hasn't started yet
    in_progress,      // Challenge is in progress
    success,          // Challenge completed successfully
    partial_success,  // Challenge completed with partial success
    failed            // Challenge failed (didn't meet success criteria)
}
