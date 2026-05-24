package com.example.counter.auth.web

data class SignupOtpRequestDTO(
    val username: String? = null,
    val email: String? = null
)

data class SignupVerifyRequestDTO(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val otp: String? = null
)

data class LoginRequestDTO(
    val username: String? = null,
    val password: String? = null
)
