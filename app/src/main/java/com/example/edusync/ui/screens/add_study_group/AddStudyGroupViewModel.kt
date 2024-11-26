package com.example.edusync.ui.screens.add_study_group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.model.Group
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
class AddGroupViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGroupUIState())
    val uiState: StateFlow<AddGroupUIState> = _uiState.asStateFlow()

    private val currentUserId = accountService.currentUserId

    fun updateName(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                nameError = validateName(name)
            )
        }
    }

    fun updateWhatsAppLink(link: String){
        _uiState.update {
            it.copy(
                whatsAppLink = link,
                linkError = validateWhatsAppLink(link)

            )
        }
    }

    fun updateDescription(description: String) {
        _uiState.update {
            it.copy(
                description = description,
                descriptionError = validateDescription(description)
            )
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name cannot be empty"
            name.length < 3 -> "Name must be at least 3 characters long"
            name.length > 50 -> "Name must be less than 50 characters"
            else -> null
        }
    }

    private fun validateDescription(description: String): String? {
        return when {
            description.isBlank() -> "Description cannot be empty"
            description.length < 10 -> "Description must be at least 10 characters long"
            description.length > 500 -> "Description must be less than 500 characters"
            else -> null
        }
    }

    private fun validateWhatsAppLink(link: String): String?{
        return when{
            link.isBlank() -> "Please provide a whatsapp link"
            link.length < 40 -> "Input a valid link"
            link.length > 100 -> "Input a valid link"
            else -> null
        }
    }

    fun createGroup() {
        val currentState = _uiState.value

        // Validate all fields
        val nameError = validateName(currentState.name)
        val descriptionError = validateDescription(currentState.description)
        val linkError = validateWhatsAppLink(currentState.whatsAppLink)

        if (nameError != null || descriptionError != null || linkError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    descriptionError = descriptionError,
                    linkError = linkError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val newGroup = Group(
                    name = currentState.name.trim(),
                    description = currentState.description.trim(),
                    members = listOf(currentUserId), // Add creator as first member
                    createdBy = currentUserId,
                    whatsAppLink = currentState.whatsAppLink.trim()
                )

                storageService.createStudyGroup(newGroup)
                _uiState.update { it.copy(isLoading = false, isGroupCreated = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to create group: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddGroupUIState() }
    }
}