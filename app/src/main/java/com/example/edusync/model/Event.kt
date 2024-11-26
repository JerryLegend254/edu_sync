package com.example.edusync.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Event(
    @DocumentId
    val id: String = "",

    // Basic event information
    val title: String = "",
    val description: String = "",

    // Event timing
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val reminderTime: Timestamp? = null,

    // Event categorization
    val type: EventType = EventType.OTHER,
    val priority: EventPriority = EventPriority.MEDIUM,

    // Location details
    val location: String = "",
    val isOnline: Boolean = false,
    val meetingLink: String = "",

    // Organization
    val courseId: String = "",  // Optional: If event is associated with a course
    val studyGroupId: String = "",  // Optional: If event is associated with a study group

    // Participant management
    val userId: String = "",  // Creator of the event
    val participants: List<String> = emptyList(),  // List of participant user IDs

    // Status tracking
    val status: EventStatus = EventStatus.UPCOMING,

    // Metadata
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    // Enum classes for event categorization
    enum class EventType {
        LECTURE,
        STUDY_GROUP,
        EXAM,
        ASSIGNMENT_DUE,
        WORKSHOP,
        MEETING,
        OFFICE_HOURS,
        OTHER
    }

    enum class EventPriority {
        HIGH,
        MEDIUM,
        LOW
    }

    enum class EventStatus {
        UPCOMING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    // Convenience methods for date handling
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalStartTime(): LocalDateTime =
        LocalDateTime.ofEpochSecond(startTime.seconds, startTime.nanoseconds, ZoneOffset.UTC)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalEndTime(): LocalDateTime =
        LocalDateTime.ofEpochSecond(endTime.seconds, endTime.nanoseconds, ZoneOffset.UTC)

    // Helper method to check if event is happening now
    fun isHappeningNow(): Boolean {
        val now = Timestamp.now()
        return now.seconds in startTime.seconds..endTime.seconds
    }

    // Helper method to check if event is overdue
    fun isOverdue(): Boolean {
        return Timestamp.now().seconds > endTime.seconds
    }

    // Helper method to get duration in minutes
    fun getDurationMinutes(): Long {
        return (endTime.seconds - startTime.seconds) / 60
    }

    // Helper method to check if reminder should be sent
    fun shouldSendReminder(): Boolean {
        if (reminderTime == null) return false
        val now = Timestamp.now()
        return now.seconds >= reminderTime.seconds && now.seconds < startTime.seconds
    }
}
