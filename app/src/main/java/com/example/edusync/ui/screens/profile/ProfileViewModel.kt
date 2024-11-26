package com.example.edusync.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.SPLASH_SCREEN
import com.example.edusync.model.UserProfile
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AccountService,
    private val storageService: StorageService
) : ViewModel() {

    private var _profileState = MutableStateFlow(UserProfile())
    val profileState: StateFlow<UserProfile> = _profileState.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authService.currentUserId
                val currentUser = storageService.getUserProfile(userId)
                if (currentUser != null) {
                    _profileState.update {
                        it.copy(
                            username = currentUser.username,
                            email = currentUser.email,
                            profilePictureUrl = currentUser.profilePictureUrl
                        )
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        username: String,
        profilePictureUrl: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authService.currentUserId
                val updatedProfile = profilePictureUrl?.let {
                    _profileState.value.copy(
                        username = username,
                        profilePictureUrl = it
                    )
                }
                if (updatedProfile != null) {
                    storageService.updateUserProfile(userId, updatedProfile)
                }
                if (updatedProfile != null) {
                    _profileState.value = updatedProfile
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun logout(restartApp: (String) -> Unit) {
        viewModelScope.launch {
            authService.signOut()
            restartApp(SPLASH_SCREEN)
        }
    }
}