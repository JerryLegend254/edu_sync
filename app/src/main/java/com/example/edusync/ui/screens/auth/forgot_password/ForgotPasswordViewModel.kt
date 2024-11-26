package com.example.edusync.ui.screens.auth.forgot_password

import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.LogService
import com.example.edusync.ui.screens.edusyncViewModel
import com.example.edusync.ui.screens.auth.ForgotPasswordUiState
import com.example.edusync.ui.screens.auth.ValidateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : edusyncViewModel(logService) {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()
    private val validateEmail: ValidateEmail = ValidateEmail()
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, message = null, isError = false) }
    }

    fun onResetClick() {
        val emailResult = validateEmail(_uiState.value.email)

        if (!emailResult.successful) {
            _uiState.update {
                it.copy(
                    message = emailResult.errorMessage,
                    isError = true
                )
            }
            return
        }

        launchCatching {
            _uiState.update { it.copy(isLoading = true, message = null, isError = false) }
            accountService.sendRecoveryEmail(uiState.value.email)
            _uiState.update { it.copy(isLoading = false, message = null, isError = false) }
        }

    }
}