package com.example.counter.challenge

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@ControllerAdvice(basePackages = ["com.example.counter.challenge"])
class ChallengeExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        val validationErrors = ex.bindingResult
            .fieldErrors
            .joinToString(", ") { error -> "${error.field}: ${error.defaultMessage}" }

        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Validation Failed",
            "message" to "Request body validation failed",
            "path" to request.getDescription(false).replace("uri=", ""),
            "validationErrors" to validationErrors
        )
        
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Invalid Request Body",
            "message" to "Request body is malformed or contains invalid JSON",
            "path" to request.getDescription(false).replace("uri=", ""),
            "details" to (ex.message ?: "")
        )

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to (ex.message ?: ""),
            "path" to request.getDescription(false).replace("uri=", "")
        )

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to (ex.message ?: ""),
            "path" to request.getDescription(false).replace("uri=", "")
        )

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Internal Server Error",
            "message" to "An unexpected error occurred",
            "path" to request.getDescription(false).replace("uri=", "")
        )

        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
