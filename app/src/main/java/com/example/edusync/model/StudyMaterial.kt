package com.example.edusync.model

import com.google.firebase.firestore.DocumentId

data class StudyMaterial(
    @DocumentId val id: String = "", // Firestore-generated ID
    val title: String = "",
    val description: String = "",
    val fileUrl: String = "", // URL to the uploaded file
    val uploadedBy: String = "", // User ID of the uploader
    val groupId: String? = null, // Optional: Group ID if shared in a group
    val subject: String = "", // Subject/Topic tag
    val createdAt: Long = System.currentTimeMillis()
)

