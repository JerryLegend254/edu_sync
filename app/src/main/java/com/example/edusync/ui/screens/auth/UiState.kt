package com.example.edusync.ui.screens.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val message: String? = null
)

data class SettingsUiState(val isAnonymousAccount: Boolean = true)