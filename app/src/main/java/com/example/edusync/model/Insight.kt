package com.example.edusync.model

import com.google.firebase.firestore.DocumentId

data class Insight(
    @DocumentId val id: String = "",
    val userId: String = "", // Reference to the User
    val tasksCompleted: Int = 0,
    val tasksOverdue: Int = 0,
    val studyHours: Int = 0, // Tracked study hours
    val lastUpdated: Long = System.currentTimeMillis()
)

