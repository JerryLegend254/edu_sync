package com.example.edusync.ui.screens.add_study_group

data class AddGroupUIState(
    val name: String = "",
    val description: String = "",
    val whatsAppLink: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isGroupCreated: Boolean = false,
    val nameError: String? = null,
    val descriptionError: String? = null,
    val linkError: String? = null
)
