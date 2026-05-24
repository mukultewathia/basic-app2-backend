package com.example.counter.email

import com.resend.Resend
import com.resend.core.exception.ResendException
import com.resend.services.emails.model.CreateEmailOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.security.MessageDigest
import kotlin.math.abs

@Service
class EmailService(@Value("\${resend.api.key}") apiKey: String) {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    private val resend = Resend(apiKey)
    private val fromEmail = "ceo@mukultewathia.com"

    fun generateOtp(email: String): String {
        val normalized = email.trim().lowercase()
        val bytes = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray())
        val intVal = ((bytes[0].toInt() and 0xFF) shl 24) or
                     ((bytes[1].toInt() and 0xFF) shl 16) or
                     ((bytes[2].toInt() and 0xFF) shl 8) or
                     (bytes[3].toInt() and 0xFF)
        val otpInt = (abs(intVal) % 9000) + 1000
        return otpInt.toString()
    }

    @Async
    fun sendOtpEmail(toEmail: String, username: String, otp: String) {
        if (toEmail.isBlank()) {
            logger.warn("No email provided, skipping OTP email.")
            return
        }

        logger.info("Preparing to send OTP email to {}", toEmail)

        try {
            val createEmailOptions = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(toEmail)
                .cc("mtewathia99@gmail.com")
                .subject("Mafia Land: Your Verification Code")
                .html("<h1>Hi $username, Verify Your Email</h1><p>Your one-time verification code is: <strong>$otp</strong></p><p>Please enter this code to complete your registration.</p>")
                .build()

            val response = resend.emails().send(createEmailOptions)
            logger.info("OTP email successfully sent! Message ID: {}", response.id)
        } catch (e: ResendException) {
            logger.error("Failed to send OTP email to {}", toEmail, e)
        } catch (e: Exception) {
            logger.error("Unexpected error sending OTP email to {}", toEmail, e)
        }
    }

    @Async
    fun sendWelcomeEmail(toEmail: String?, username: String) {
        if (toEmail.isNullOrBlank()) {
            logger.warn("No email provided for user {}, skipping welcome email.", username)
            return
        }

        logger.info("Preparing to send welcome email to {}", toEmail)

        try {
            val createEmailOptions = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(toEmail)
                .subject("Mafia Land: Welcome!")
                .html("<h1>Welcome to Mafia Land, $username!</h1><p>We are thrilled to have you on board. Start tracking your metrics and habits today!</p>")
                .build()

            val response = resend.emails().send(createEmailOptions)
            logger.info("Welcome email successfully sent! Message ID: {}", response.id)
        } catch (e: ResendException) {
            logger.error("Failed to send welcome email to {}", toEmail, e)
        } catch (e: Exception) {
            logger.error("Unexpected error sending welcome email to {}", toEmail, e)
        }
    }
}
