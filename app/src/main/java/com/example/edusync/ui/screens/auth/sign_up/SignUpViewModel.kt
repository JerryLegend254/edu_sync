package com.example.edusync.ui.screens.auth.sign_up

import android.util.Log
import com.example.edusync.HOME_SCREEN
import com.example.edusync.SIGNUP_SCREEN
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.LogService
import com.example.edusync.model.service.StorageService
import com.example.edusync.ui.screens.edusyncViewModel
import com.example.edusync.ui.screens.auth.SignUpUiState
import com.example.edusync.ui.screens.auth.ValidateEmail
import com.example.edusync.ui.screens.auth.ValidateName
import com.example.edusync.ui.screens.auth.ValidatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    logService: LogService
) : edusyncViewModel(logService) {

    private val validateEmail: ValidateEmail = ValidateEmail()
    private val validatePassword: ValidatePassword = ValidatePassword()
    private val validateName: ValidateName = ValidateName()
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, error = null) }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        val nameResult = validateName(_uiState.value.name)
        val emailResult = validateEmail(_uiState.value.email)
        val passwordResult = validatePassword(_uiState.value.password)

        val state = _uiState.value

        when {
            !nameResult.successful -> {
                _uiState.update { it.copy(error = nameResult.errorMessage) }
                return
            }

            !emailResult.successful -> {
                _uiState.update { it.copy(error = emailResult.errorMessage) }
                return
            }

            !passwordResult.successful -> {
                _uiState.update { it.copy(error = passwordResult.errorMessage) }
                return
            }

            state.password != state.confirmPassword -> {
                _uiState.update { it.copy(error = "Passwords don't match") }
                return
            }
        }


        launchCatching {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val uid  = accountService.signUp(state.email, state.password)
            storageService.initUserProfile(state.email,state.name, uid)
            Log.d("SignUpVM", uid)
            _uiState.update { it.copy(isLoading = false, error = null) }
            openAndPopUp(HOME_SCREEN, SIGNUP_SCREEN)

        }

    }
}