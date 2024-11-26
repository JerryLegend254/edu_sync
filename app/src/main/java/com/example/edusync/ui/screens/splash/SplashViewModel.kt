package com.example.edusync.ui.screens.splash

import androidx.compose.runtime.mutableStateOf
import com.example.edusync.LOGIN_SCREEN
import com.example.edusync.SPLASH_SCREEN
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.LogService
import com.example.edusync.HOME_SCREEN
import com.example.edusync.ui.screens.edusyncViewModel
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : edusyncViewModel(logService) {
    val showError = mutableStateOf(false)


    fun onAppStart(openAndPopUp: (String, String) -> Unit) {

        showError.value = false
        if (accountService.hasUser) openAndPopUp(HOME_SCREEN, SPLASH_SCREEN)
        else redirectToAuthStack(openAndPopUp)
    }

    private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        launchCatching(snackbar = false) {
            try {
                accountService.createAnonymousAccount()
            } catch (ex: FirebaseAuthException) {
                showError.value = true
                throw ex
            }
            openAndPopUp(HOME_SCREEN, SPLASH_SCREEN)
        }
    }

    private fun redirectToAuthStack(openAndPopUp: (String, String) -> Unit){
        openAndPopUp(LOGIN_SCREEN, SPLASH_SCREEN)
    }
}