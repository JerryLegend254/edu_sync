package com.example.edusync.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edusync.ADD_TASK_SCREEN
import com.example.edusync.JOIN_STUDY_GROUP_SCREEN
import com.example.edusync.PROFILE_SCREEN
import com.example.edusync.model.Document
import com.example.edusync.model.Event
import com.example.edusync.model.Task
import com.example.edusync.model.TaskPriority
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    openScreen: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val onNavigateToAddTask = { openScreen(ADD_TASK_SCREEN) }
    val onNavigateToJoinGroup = {openScreen(JOIN_STUDY_GROUP_SCREEN)}
    val onNavigateToUploadDocument = {}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EduSync Dashboard") },
                actions = {
                    IconButton(onClick = { openScreen(PROFILE_SCREEN) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error!!,
                    onDismiss = viewModel::clearError
                )
            }

            else -> {
                HomeContent(
                    modifier = modifier.padding(paddingValues),
                    uiState = uiState,
                    onAddTask = onNavigateToAddTask,
                    onJoinGroup = onNavigateToJoinGroup,
                    onUploadDocument = onNavigateToUploadDocument
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onAddTask: () -> Unit,
    onJoinGroup: () -> Unit,
    onUploadDocument: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Actions
        // Today's Tasks
        item {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(uiState.tasks) { task ->
            TaskCard(task = task)
        }

        item {
            QuickActions(
                onAddTask = onAddTask,
                onJoinGroup = onJoinGroup,
                onUploadDocument = onUploadDocument
            )
        }

        items(uiState.events) { event ->
            EventCard(event = event)
        }

        // Recent Documents
        if (uiState.recentDocuments.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Documents",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(uiState.recentDocuments) { document ->
                DocumentCard(document = document)
            }
        }
    }
}

@Composable
private fun QuickActions(
    modifier: Modifier = Modifier,
    onAddTask: () -> Unit,
    onJoinGroup: () -> Unit,
    onUploadDocument: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add Task",
                    onClick = onAddTask
                )
                QuickActionButton(
                    icon = Icons.Default.Group,
                    label = "Join Group",
                    onClick = onJoinGroup
                )
                QuickActionButton(
                    icon = Icons.Default.Upload,
                    label = "Upload",
                    onClick = onUploadDocument
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (task.priority) {
                        TaskPriority.HIGH -> MaterialTheme.colorScheme.errorContainer
                        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
                        TaskPriority.LOW -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Text(
                    text = task.priority.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventCard(
    event: Event,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Badge(
                    containerColor = when (event.type) {
                        Event.EventType.LECTURE -> MaterialTheme.colorScheme.primaryContainer
                        Event.EventType.STUDY_GROUP -> MaterialTheme.colorScheme.secondaryContainer
                        Event.EventType.EXAM -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(event.type.name)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${
                        event.getLocalStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    } - ${
                        event.getLocalEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (event.location.isNotEmpty() || event.isOnline) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (event.isOnline) Icons.Default.VideoCall else Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (event.isOnline) "Online Meeting" else event.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: Document,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (document.fileType) {
                        Document.FileType.PDF -> Icons.Default.PictureAsPdf
                        Document.FileType.DOCUMENT -> Icons.Default.Description
                        Document.FileType.SPREADSHEET -> Icons.Default.TableChart
                        Document.FileType.PRESENTATION -> Icons.Default.Slideshow
                        else -> Icons.Default.InsertDriveFile
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Column {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = document.getUploadDateFormatted(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            FilledTonalIconButton(onClick = { /* Open document */ }) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open document"
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Alert(
        modifier = modifier.padding(16.dp),
        onDismiss = onDismiss,
        title = "Error",
        text = message,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
private fun Alert(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    text: String,
    confirmButton: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                confirmButton()
            }
        }
    }
}