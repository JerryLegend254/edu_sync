package com.example.edusync.model

import com.google.firebase.firestore.DocumentId

data class Group(
    val id: String = "", // Firestore-generated ID
    val name: String = "",
    val description: String = "",
    val members: List<String> = emptyList(), // List of User IDs
    val createdBy: String = "", // User ID of the creator
    val createdAt: Long = System.currentTimeMillis(),
    val whatsAppLink: String = ""
)

