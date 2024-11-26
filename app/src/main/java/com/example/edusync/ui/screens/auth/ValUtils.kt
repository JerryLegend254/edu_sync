package com.example.edusync.ui.screens.auth

import android.util.Patterns

class ValidateEmail {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Email cannot be blank"
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }
        return ValidationResult(successful = true)
    }
}

class ValidatePassword {
    operator fun invoke(password: String): ValidationResult {
        if (password.length < 8) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must be at least 8 characters"
            )
        }
        val containsLettersAndDigits = password.any { it.isLetter() } &&
                password.any { it.isDigit() }
        if (!containsLettersAndDigits) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must contain at least one letter and digit"
            )
        }
        return ValidationResult(successful = true)
    }
}

class ValidateName {
    operator fun invoke(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Name cannot be blank"
            )
        }
        if (name.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "Name must be at least 2 characters"
            )
        }
        return ValidationResult(successful = true)
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)