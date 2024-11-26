package com.example.edusync.ui.screens.study_groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinGroupUIState())
    val uiState: StateFlow<JoinGroupUIState> = _uiState.asStateFlow()

    private val currentUserId = accountService.currentUserId

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Collect the Flow of groups and transform it
                storageService.getAllStudyGroups()
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Failed to load groups: ${e.message}"
                            )
                        }
                    }
                    .collect { groups ->
                        val groupUIs = groups.map { group ->
                            GroupUI(
                                id = group.id,
                                name = group.name,
                                description = group.description,
                                memberCount = group.members.size,
                                isCurrentUserMember = group.members.contains(currentUserId),
                                whatsAppLink = group.whatsAppLink
                            )
                        }
                        _uiState.update {
                            it.copy(
                                groups = groupUIs,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load groups: ${e.message}"
                    )
                }
            }
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isJoiningGroup = groupId, errorMessage = null) }

            try {
                storageService.joinStudyGroup(groupId, currentUserId)

                // Update the local state to reflect the change
                _uiState.update { currentState ->
                    val updatedGroups = currentState.groups.map { group ->
                        if (group.id == groupId) {
                            group.copy(
                                memberCount = group.memberCount + 1,
                                isCurrentUserMember = true
                            )
                        } else {
                            group
                        }
                    }
                    currentState.copy(
                        groups = updatedGroups,
                        isJoiningGroup = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isJoiningGroup = null,
                        errorMessage = "Failed to join group: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshGroups() {
        loadGroups()
    }
}