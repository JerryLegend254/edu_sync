package com.example.edusync.model

import com.google.firebase.firestore.DocumentId

data class Task(
    val id: String = "", // Firestore-generated ID
    val title: String = "",
    val description: String = "",
    val userId: String = "", // Reference to the User who created the task
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: String = "", // e.g., "Assignment", "Exam", "Event"
    val dueDate: Long = 0L, // Timestamp
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskPriority {
    HIGH,
    MEDIUM,
    LOW
}

