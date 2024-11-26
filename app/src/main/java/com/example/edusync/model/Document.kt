package com.example.edusync.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Document(
    @DocumentId
    val id: String = "",

    // Basic document information
    val title: String = "",
    val description: String = "",

    // File details
    val fileName: String = "",
    val fileUrl: String = "",
    val fileType: FileType = FileType.OTHER,
    val fileSize: Long = 0, // in bytes
    val mimeType: String = "",

    // Organization and categorization
    val courseId: String = "",  // Optional: Associated course
    val studyGroupId: String = "",  // Optional: Associated study group
    val tags: List<String> = emptyList(),
    val category: DocumentCategory = DocumentCategory.OTHER,

    // Access control
    val userId: String = "",  // Owner/uploader
    val visibility: Visibility = Visibility.PRIVATE,
    val sharedWithUserIds: List<String> = emptyList(),
    val sharedWithGroupIds: List<String> = emptyList(),

    // Version control
    val version: Int = 1,
    val previousVersions: List<String> = emptyList(), // List of previous version document IDs

    // Metadata
    val uploadDate: Timestamp = Timestamp.now(),
    val lastModified: Timestamp = Timestamp.now(),
    val lastAccessedDate: Timestamp? = null,

    // Engagement metrics
    val downloadCount: Int = 0,
    val viewCount: Int = 0,
    val likes: Int = 0,
    val comments: List<Comment> = emptyList()
) {
    // File types supported by the application
    enum class FileType {
        PDF,
        DOCUMENT, // Word, Google Docs
        SPREADSHEET,
        PRESENTATION,
        IMAGE,
        VIDEO,
        AUDIO,
        CODE,
        OTHER
    }

    // Document categories for organization
    enum class DocumentCategory {
        LECTURE_NOTES,
        ASSIGNMENT,
        STUDY_GUIDE,
        RESEARCH_PAPER,
        PROJECT,
        EXAM_PREP,
        REFERENCE_MATERIAL,
        OTHER
    }

    // Visibility levels for access control
    enum class Visibility {
        PRIVATE,         // Only owner can access
        SHARED,          // Accessible by specific users/groups
        PUBLIC          // Accessible by all EduSync users
    }

    // Data class for comments
    data class Comment(
        val id: String = "",
        val userId: String = "",
        val content: String = "",
        val timestamp: Timestamp = Timestamp.now(),
        val likes: Int = 0
    )

    // Util funcs
    fun isOwnedBy(userId: String): Boolean = this.userId == userId

    fun canBeAccessedBy(userId: String): Boolean {
        return when {
            this.userId == userId -> true
            visibility == Visibility.PUBLIC -> true
            visibility == Visibility.SHARED -> {
                userId in sharedWithUserIds ||
                        sharedWithGroupIds.isNotEmpty()
            }
            else -> false
        }
    }

    fun getFormattedFileSize(): String {
        return when {
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
            fileSize < 1024 * 1024 * 1024 -> "${fileSize / (1024 * 1024)} MB"
            else -> "${fileSize / (1024 * 1024 * 1024)} GB"
        }
    }

    fun isVersioned(): Boolean = version > 1 || previousVersions.isNotEmpty()

    fun hasComments(): Boolean = comments.isNotEmpty()

    fun getUploadDateFormatted(): String {
        // You can implement your preferred date formatting here
        return uploadDate.toDate().toString()
    }

    fun isRecent(daysThreshold: Int = 7): Boolean {
        val now = Timestamp.now()
        val differenceInDays = (now.seconds - uploadDate.seconds) / (24 * 60 * 60)
        return differenceInDays <= daysThreshold
    }

    companion object {
        // Common MIME types
        const val MIME_TYPE_PDF = "application/pdf"
        const val MIME_TYPE_DOC = "application/msword"
        const val MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        const val MIME_TYPE_XLS = "application/vnd.ms-excel"
        const val MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        const val MIME_TYPE_PPT = "application/vnd.ms-powerpoint"
        const val MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation"

        // Maximum file size (e.g., 50MB)
        const val MAX_FILE_SIZE = 50L * 1024 * 1024 // in bytes
    }
}