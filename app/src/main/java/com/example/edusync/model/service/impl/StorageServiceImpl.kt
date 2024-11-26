package com.example.edusync.model.service.impl

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.edusync.model.Document
import com.example.edusync.model.Event
import com.example.edusync.model.Group
import com.example.edusync.model.Task
import com.example.edusync.model.UserProfile
import com.example.edusync.model.service.AccountService
import com.example.edusync.model.service.StorageService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
) : StorageService {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTodayTasks(userId: String): Flow<List<Task>> = callbackFlow {
        val today = LocalDate.now()
        val query = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(tasks)
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun addTask(task: Task): String {
        return firestore.collection(TASKS_COLLECTION)
            .add(task)
            .await()
            .id
    }

    override suspend fun updateTask(task: Task) {
        firestore.collection(TASKS_COLLECTION)
            .document(task.id)
            .set(task)
            .await()
    }

    override suspend fun deleteTask(taskId: String) {
        firestore.collection(TASKS_COLLECTION)
            .document(taskId)
            .delete()
            .await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTodayEvents(userId: String): Flow<List<Event>> = callbackFlow {
        val today = LocalDate.now()
        val query = firestore.collection(EVENTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("date", today.toString())
            .orderBy("startTime")

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val events = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Event::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(events)
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun addEvent(event: Event): String {
        return firestore.collection(EVENTS_COLLECTION)
            .add(event)
            .await()
            .id
    }

    override fun getAllStudyGroups(): Flow<List<Group>> = callbackFlow {
        val query = firestore.collection(STUDY_GROUPS_COLLECTION)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val groups = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Group::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(groups)
        }

        awaitClose { subscription.remove() }
    }

    override fun getStudyGroups(userId: String): Flow<List<Group>> = callbackFlow {
        val query = firestore.collection(STUDY_GROUPS_COLLECTION)
            .whereArrayContains("memberIds", userId)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val groups = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Group::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(groups)
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun joinStudyGroup(groupId: String, userId: String) {
        firestore.collection(STUDY_GROUPS_COLLECTION)
            .document(groupId)
            .update("memberIds", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun createStudyGroup(group: Group): String {
        return firestore.collection(STUDY_GROUPS_COLLECTION)
            .add(group)
            .await()
            .id
    }

    override suspend fun uploadDocument(document: Document): String {
        return firestore.collection(DOCUMENTS_COLLECTION)
            .add(document)
            .await()
            .id
    }

    override fun getUserDocuments(userId: String): Flow<List<Document>> = callbackFlow {
        val query = firestore.collection(DOCUMENTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("uploadDate", Query.Direction.DESCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val documents = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Document::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            trySend(documents)
        }

        awaitClose { subscription.remove() }
    }


    override suspend fun initUserProfile(email: String, username: String, userId: String) {
        firestore.collection(USER_COLLECTION).document(userId)
            .set(UserProfile(email, username, profilePictureUrl = "")).await()
    }

    override suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile) {
        firestore.collection(USER_COLLECTION)
            .document(userId)
            .set(updatedProfile)
            .await()


    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return firestore.collection(USER_COLLECTION).document(userId).get().await().toObject()
    }

    companion object {
        private const val TASKS_COLLECTION = "tasks"
        private const val EVENTS_COLLECTION = "events"
        private const val STUDY_GROUPS_COLLECTION = "study_groups"
        private const val DOCUMENTS_COLLECTION = "documents"
        private const val USER_COLLECTION = "users"
    }
}

fun removeCurlyBrackets(id: String): String {
    return id.removePrefix("{").removeSuffix("}")
}