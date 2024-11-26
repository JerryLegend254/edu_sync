package com.example.edusync.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun BasicToolbar(@StringRes title: Int) {
    TopAppBar(title = { Text(stringResource(title)) }, backgroundColor = toolbarColor())
}

@Composable
fun ActionToolbar(
    @StringRes title: Int?,
    stringAlt: String?,
    endActionIcon: ImageVector?,
    modifier: Modifier,
    endAction: () -> Unit
) {
    var useTitle: String = ""
    if (title != null) {
        useTitle = stringResource(title)
    } else {
        if (stringAlt != null) {
            useTitle = stringAlt
        }
    }

    TopAppBar(
        title = { Text(useTitle) },
        backgroundColor = toolbarColor(),
        actions = {
            Box(modifier) {
                IconButton(onClick = endAction) {
                    if (endActionIcon != null) {
                        Icon(endActionIcon, contentDescription = "Action")
                    }
                }
            }
        }
    )
}

@Composable
private fun toolbarColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
}