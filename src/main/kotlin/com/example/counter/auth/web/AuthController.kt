package com.example.counter.auth.web

import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.counter.auth.jwt.JwtService
import com.example.counter.auth.security.CurrentUser
import com.example.counter.email.EmailService
import com.example.counter.user.User
import com.example.counter.user.UserRepository
import jakarta.servlet.http.HttpServletResponse
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth/")
class AuthController(
        private val jwt: JwtService,
        private val users: UserRepository,
        private val emailService: EmailService
) {
    private val emailRegex = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""".toRegex()
    private val rateLimits = ConcurrentHashMap<String, RateLimitInfo>()

    private data class RateLimitInfo(
        val attemptCount: Int,
        val lastRequestTime: Instant
    )

    private fun checkRateLimit(email: String): ResponseEntity<*>? {
        val now = Instant.now()
        var allowed = false
        var remainingWaitSeconds = 0L

        rateLimits.compute(email) { _, info ->
            if (info == null || Duration.between(info.lastRequestTime, now).toMinutes() >= 15) {
                allowed = true
                RateLimitInfo(1, now)
            } else {
                val exponent = info.attemptCount - 1
                val delaySeconds = (30L * Math.pow(2.0, exponent.toDouble()).toLong()).coerceAtMost(3600L)
                val nextAllowedTime = info.lastRequestTime.plusSeconds(delaySeconds)

                if (now.isBefore(nextAllowedTime)) {
                    allowed = false
                    remainingWaitSeconds = Duration.between(now, nextAllowedTime).toSeconds()
                    info
                } else {
                    allowed = true
                    RateLimitInfo(info.attemptCount + 1, now)
                }
            }
        }

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(mapOf(
                    "error" to "Too many OTP requests. Please try again in $remainingWaitSeconds seconds.",
                    "retryAfterSeconds" to remainingWaitSeconds
                ))
        }
        return null
    }

    /**
     * Request a sign-up OTP for a given email and username.
     * Validates that the fields are not blank and that the username/email are not already registered.
     */
    @PostMapping("/signup/otp")
    fun requestOtp(@RequestBody body: SignupOtpRequestDTO): ResponseEntity<*> {
        val username = body.username?.trim()?.lowercase()
        val email = body.email?.trim()?.lowercase()

        val validationError = validateSignup(username, email)
        if (validationError != null) return validationError

        val rateLimitError = checkRateLimit(email!!)
        if (rateLimitError != null) return rateLimitError

        sendOtp(email, username!!)

        return ResponseEntity.ok(mapOf("ok" to true, "message" to "OTP sent successfully"))
    }

    /**
     * Validates that the username and email are not blank and do not already exist in the database.
     * Returns a ResponseEntity with the error if invalid, or null if valid.
     */
    private fun validateSignup(username: String?, email: String?): ResponseEntity<*>? {
        if (username.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Username required"))
        }
        if (email.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Email required"))
        }
        if (!emailRegex.matches(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Invalid email format"))
        }

        if (users.findByUsername(username).isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mapOf("error" to "Username exists"))
        }
        if (users.findByEmail(email).isPresent) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mapOf("error" to "Email already registered"))
        }
        return null
    }

    /**
     * Helper method to generate and send the OTP to the user's email.
     */
    private fun sendOtp(email: String, username: String) {
        val otp = emailService.generateOtp(email)
        emailService.sendOtpEmail(email, username, otp)
    }

    /**
     * Verifies the provided OTP, validates the username and email, and saves the new User.
     * Sends a welcome email upon successful registration.
     */
    @PostMapping("/signup")
    fun register(@RequestBody body: SignupVerifyRequestDTO): ResponseEntity<*> {
        val username = body.username?.trim()?.lowercase()
        val email = body.email?.trim()?.lowercase()
        val password = body.password
        val providedOtp = body.otp?.trim()

        if (password.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Password required"))
        }
        if (providedOtp.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "OTP required"))
        }

        val validationError = validateSignup(username, email)
        if (validationError != null) return validationError

        val expectedOtp = emailService.generateOtp(email!!)
        if (providedOtp != expectedOtp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Invalid OTP"))
        }

        val u = users.save(User(username!!, email, password))
        emailService.sendWelcomeEmail(email, username)

        return ResponseEntity.ok(mapOf("ok" to true, "id" to u.userId, "email" to (u.email ?: "")))
    }

    /**
     * Authenticates a user with a username and password.
     * Generates JWT access and refresh tokens, sets the refresh token in a cookie, and returns user details.
     */
    @PostMapping("/login")
    fun login(@RequestBody body: LoginRequestDTO, res: HttpServletResponse): ResponseEntity<*> {
        val username = body.username?.trim() ?: ""
        val userOpt = users.findByUsername(username)
        if (userOpt.isEmpty) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Invalid credentials"))
        }
        val user = userOpt.get()

        if (user.password != body.password) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Invalid credentials"))
        }

        val access = jwt.generateToken(user.username, user.userId)
        val refresh = jwt.generateRefreshToken(user.username, user.userId)

        res.addHeader(
                HttpHeaders.SET_COOKIE,
                CookieUtil.refreshCookie(refresh, Duration.ofDays(1).toSeconds()).toString()
        )

        return ResponseEntity.ok(
                mapOf(
                        "id" to user.userId,
                        "username" to user.username,
                        "email" to (user.email ?: ""),
                        "accessToken" to access,
                        "expiresIn" to Duration.ofMinutes(30).toSeconds()
                )
        )
    }

    /**
     * Logs out the user by clearing the refresh token cookie.
     */
    @PostMapping("/logout")
    fun logout(res: HttpServletResponse): ResponseEntity<*> {
        res.addHeader(HttpHeaders.SET_COOKIE, CookieUtil.refreshCookie("", 0).toString())
        return ResponseEntity.ok(mapOf("ok" to true, "message" to "Logged out successfully"))
    }

    /**
     * Validates the refresh token cookie and issues a new access token if valid.
     */
    @PostMapping("/refresh")
    fun refresh(
            @CookieValue(name = "refresh_token", required = false) refresh: String?
    ): ResponseEntity<*> {
        if (refresh.isNullOrBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
        }

        val username =
                try {
                    jwt.validateAndGetSubject(refresh)
                } catch (e: TokenExpiredException) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(mapOf("error" to "Refresh token expired"))
                } catch (e: Exception) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(mapOf("error" to "Invalid token"))
                }

        val uOpt = users.findByUsername(username)
        if (uOpt.isEmpty) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
        }
        val u = uOpt.get()

        val access = jwt.generateToken(u.username, u.userId)
        return ResponseEntity.ok(
                mapOf(
                        "ok" to true,
                        "username" to u.username,
                        "userId" to u.userId,
                        "email" to (u.email ?: ""),
                        "accessToken" to access,
                        "expiresIn" to Duration.ofMinutes(30).toSeconds()
                )
        )
    }

    /**
     * Retrieves the currently logged-in user's details based on the authentication context.
     */
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<*> {
        val username = CurrentUser.getCurrentUsername()
        val userId = CurrentUser.getCurrentUserId()

        if (username == null || userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Not authenticated"))
        }

        val userOpt = users.findByUserId(userId)
        val email = userOpt.map { it.email }.orElse("")

        return ResponseEntity.ok(
                mapOf("username" to username, "userId" to userId, "email" to (email ?: ""))
        )
    }
}
