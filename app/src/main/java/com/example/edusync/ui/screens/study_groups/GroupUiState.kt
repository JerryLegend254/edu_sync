package com.example.edusync.ui.screens.study_groups

data class JoinGroupUIState(
    val groups: List<GroupUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isJoiningGroup: String? = null // GroupId that's currently being joined
)

data class GroupUI(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val isCurrentUserMember: Boolean,
    val whatsAppLink: String
)
