package com.example.edusync.ui.screens.home

import com.example.edusync.model.Document
import com.example.edusync.model.Event
import com.example.edusync.model.Task

data class HomeUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val events: List<Event> = emptyList(),
    val recentDocuments: List<Document> = emptyList(),
    val error: String? = null
)
