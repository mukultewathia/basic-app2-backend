package com.example.counter.challenge

import com.example.counter.user.User
import com.example.counter.user.UserRepository
import com.example.counter.habit.Habit
import com.example.counter.habit.HabitRepository
import com.example.counter.habit.HabitEntryRepository
import com.example.counter.notes.NoteRepository
import com.example.counter.challenge.ChallengeDto.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDate
import java.util.Optional

class ChallengeServiceTest {

    private val challengeRepo: ChallengeRepository = mock()
    private val challengeHabitsRepo: ChallengeHabitsRepository = mock()
    private val habitRepo: HabitRepository = mock()
    private val habitEntryRepo: HabitEntryRepository = mock()
    private val userRepo: UserRepository = mock()
    private val noteRepo: NoteRepository = mock()

    private lateinit var challengeService: ChallengeService
    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        challengeService = ChallengeService(
            challengeRepo,
            challengeHabitsRepo,
            habitRepo,
            habitEntryRepo,
            userRepo,
            noteRepo
        )
        testUser = User().apply {
            username = "testuser"
        }
        setField(testUser, "userId", 1L)
    }

    @Test
    fun `test create challenge with description successfully`() {
        // Arrange
        val request = ChallengeCreateRequest(
            name = "Yoga Challenge",
            habitIds = listOf(10L, 20L),
            startDate = LocalDate.now(),
            durationDays = 30,
            challengeDescription = "Do daily yoga for 30 days"
        )

        val habit1 = Habit().apply {
            name = "Yoga Morning"
            user = testUser
        }
        setField(habit1, "habitId", 10L)

        val habit2 = Habit().apply {
            name = "Yoga Evening"
            user = testUser
        }
        setField(habit2, "habitId", 20L)

        whenever(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser))
        whenever(challengeRepo.findByUsernameAndNameAndStartDateAndNotDeleted(eq("testuser"), eq(request.name), eq(request.startDate)))
            .thenReturn(Optional.empty())
        whenever(habitRepo.findHabitsByIdsAndUsername(eq(request.habitIds), eq("testuser")))
            .thenReturn(listOf(habit1, habit2))
        
        whenever(challengeRepo.save(any<Challenge>())).thenAnswer { invocation ->
            val challenge = invocation.arguments[0] as Challenge
            challenge.challengeId = 100L
            challenge
        }

        // Act
        val response = challengeService.createChallenge("testuser", request)

        // Assert
        assertNotNull(response)
        assertEquals(100L, response.challengeId)
        assertEquals("Yoga Challenge", response.name)
        assertEquals("Do daily yoga for 30 days", response.challengeDescription)
        
        verify(challengeRepo).save(any<Challenge>())
        verify(challengeHabitsRepo, times(2)).save(any<ChallengeHabits>())
    }

    @Test
    fun `test create challenge unique validation error`() {
        // Arrange
        val request = ChallengeCreateRequest(
            name = "Duplicate Challenge",
            habitIds = listOf(10L),
            startDate = LocalDate.now(),
            durationDays = 30
        )

        whenever(userRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser))
        whenever(challengeRepo.findByUsernameAndNameAndStartDateAndNotDeleted(eq("testuser"), eq(request.name), eq(request.startDate)))
            .thenReturn(Optional.of(Challenge()))

        // Act & Assert
        val exception = assertThrows(RuntimeException::class.java) {
            challengeService.createChallenge("testuser", request)
        }
        assertTrue(exception.message!!.contains("already exists"))
        verify(challengeRepo, never()).save(any())
    }

    @Test
    fun `test update challenge description and retrospective successfully`() {
        // Arrange
        val challengeId = 100L
        val existingChallenge = Challenge(testUser, "Old Name", LocalDate.now(), 15).apply {
            this.challengeId = challengeId
            this.challengeDescription = "Old Description"
            this.retrospective = null
        }

        val request = ChallengeUpdateRequest(
            name = "New Name",
            startDate = null,
            durationDays = 20,
            challengeDescription = "New Description",
            retrospective = "Completed successfully!"
        )

        whenever(challengeRepo.findByUsernameAndChallengeIdAndNotDeleted("testuser", challengeId))
            .thenReturn(Optional.of(existingChallenge))
        whenever(challengeRepo.save(any<Challenge>())).thenAnswer { it.arguments[0] as Challenge }

        // Act
        val response = challengeService.updateChallenge("testuser", challengeId, request)

        // Assert
        assertNotNull(response)
        assertEquals("New Name", response.name)
        assertEquals("New Description", response.challengeDescription)
        assertEquals("Completed successfully!", response.retrospective)
        
        verify(challengeRepo).save(existingChallenge)
    }

    private fun setField(target: Any, fieldName: String, value: Any?) {
        val field = target.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(target, value)
    }
}
