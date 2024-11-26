package com.example.edusync.model.service

import android.net.Uri
import com.example.edusync.model.Document
import com.example.edusync.model.Event
import com.example.edusync.model.Group
import com.example.edusync.model.Task
import com.example.edusync.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface StorageService {
    // Task related operations
    fun getTodayTasks(userId: String): Flow<List<Task>>
    suspend fun addTask(task: Task): String
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)

    // Event related operations
    fun getTodayEvents(userId: String): Flow<List<Event>>
    suspend fun addEvent(event: Event): String

    // Study Group operations
    fun getAllStudyGroups() : Flow<List<Group>>
    fun getStudyGroups(userId: String): Flow<List<Group>>
    suspend fun joinStudyGroup(groupId: String, userId: String)
    suspend fun createStudyGroup(group: Group) : String

    // Document operations
    suspend fun uploadDocument(document: Document): String
    fun getUserDocuments(userId: String): Flow<List<Document>>


    suspend fun initUserProfile(email: String, username: String, userId: String)
    suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile)
    suspend fun getUserProfile(userId: String) : UserProfile?

}