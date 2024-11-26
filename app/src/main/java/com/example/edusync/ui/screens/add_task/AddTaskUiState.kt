package com.example.edusync.ui.screens.add_task

import com.example.edusync.model.TaskPriority

data class AddTaskUIState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: String = "",
    val dueDate: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTaskAdded: Boolean = false
)

