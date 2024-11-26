package com.example.edusync.ui.screens.auth.login

import com.example.edusync.HOME_SCREEN
import com.example.edusync.LOGIN_SCREEN
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.LogService
import com.example.edusync.ui.screens.edusyncViewModel
import com.example.edusync.ui.screens.auth.LoginUiState
import com.example.edusync.ui.screens.auth.ValidateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : edusyncViewModel(logService) {

    private val validateEmail: ValidateEmail = ValidateEmail()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        val emailResult = validateEmail(_uiState.value.email)

        if (!emailResult.successful) {
            _uiState.update { it.copy(error = emailResult.errorMessage) }
            return
        }

        if (uiState.value.password.isBlank()) {
            _uiState.update { it.copy(error = "Password can't be blank") }
            return
        }

        launchCatching {
            _uiState.update { it.copy(isLoading = true, error = null) }
            accountService.authenticate(uiState.value.email, uiState.value.password)
            _uiState.update { it.copy(isLoading = false, error = null) }
            openAndPopUp(HOME_SCREEN, LOGIN_SCREEN)
        }
    }
}