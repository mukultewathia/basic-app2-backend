package com.example.counter.challenge

import com.example.counter.user.User
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "challenges")
class Challenge {
    @Id
    @Column(name = "challenge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var challengeId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @Column(nullable = false)
    var name: String = ""

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate = LocalDate.now()

    @Column(name = "duration_days", nullable = false)
    var durationDays: Int = 0

    @Column(name = "end_date", nullable = false, insertable = false, updatable = false)
    var endDate: LocalDate? = null

    @Column(name = "challenge_description")
    var challengeDescription: String? = null

    @Column(name = "retrospective")
    var retrospective: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false, columnDefinition = "challenge_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var scheduleStatus: ChallengeStatus = ChallengeStatus.scheduled

    @Enumerated(EnumType.STRING)
    @Column(name = "completion", nullable = false, columnDefinition = "completion_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var completion: CompletionStatus = CompletionStatus.not_started

    @Column(name = "success_percent", nullable = false)
    var successPercent: Int = 0

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = getIndiaTimestamp()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = getIndiaTimestamp()

    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    var challengeHabits: MutableList<ChallengeHabits> = mutableListOf()

    // Constructors
    constructor() {
        this.createdAt = getIndiaTimestamp()
        this.updatedAt = getIndiaTimestamp()
    }

    constructor(user: User, name: String, startDate: LocalDate, durationDays: Int) {
        this.user = user
        this.name = name
        this.startDate = startDate
        this.durationDays = durationDays
        this.createdAt = getIndiaTimestamp()
        this.updatedAt = getIndiaTimestamp()
        updateStatus(durationDays)
    }

    // Helper method to get current timestamp in India timezone
    private fun getIndiaTimestamp(): OffsetDateTime {
        val indiaZone = ZoneId.of("Asia/Kolkata")
        return OffsetDateTime.now(indiaZone)
    }

    // Method to update challenge status based on current date
    fun updateStatus(durationDays: Int) {
        val today = LocalDate.now()
        
        if (startDate.isAfter(today)) {
            this.scheduleStatus = ChallengeStatus.scheduled
        } else if (startDate.isEqual(today) || (startDate.isBefore(today) && startDate.plusDays(durationDays.toLong()).isAfter(today))) {
            this.scheduleStatus = ChallengeStatus.active
        } else {
            this.scheduleStatus = ChallengeStatus.expired
        }
        
        this.updatedAt = getIndiaTimestamp()
    }
}
