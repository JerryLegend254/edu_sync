package com.example.edusync.ui.screens.add_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.model.Task
import com.example.edusync.model.TaskPriority
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
class AddTaskViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUIState())
    val uiState: StateFlow<AddTaskUIState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updatePriority(priority: TaskPriority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateDueDate(dueDate: Long) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }

    fun addTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val currentState = _uiState.value
                if (currentState.title.isBlank()) {
                    throw IllegalStateException("Title cannot be empty")
                }

                val task = Task(
                    title = currentState.title,
                    description = currentState.description,
                    userId = accountService.currentUserId,
                    priority = currentState.priority,
                    category = currentState.category,
                    dueDate = currentState.dueDate
                )

                storageService.addTask(task)
                _uiState.update { it.copy(isLoading = false, isTaskAdded = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to add task"
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddTaskUIState() }
    }
}
