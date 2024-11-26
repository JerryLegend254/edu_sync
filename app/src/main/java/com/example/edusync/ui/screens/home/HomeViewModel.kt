package com.example.edusync.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.model.Document
import com.example.edusync.model.Task
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                val userId = accountService.currentUserId

                combine(
                    storageService.getTodayTasks(userId),
                    storageService.getTodayEvents(userId),
                    storageService.getUserDocuments(userId)
                ) { tasks, events, documents ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            tasks = tasks,
                            events = events,
                            recentDocuments = documents.take(5),
                            error = null
                        )
                    }
                }.collect {}
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                storageService.addTask(task)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun joinStudyGroup(groupId: String) {
        viewModelScope.launch {
            try {
                storageService.joinStudyGroup(groupId, accountService.currentUserId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun uploadDocument(document: Document) {
        viewModelScope.launch {
            try {
                storageService.uploadDocument(document)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}