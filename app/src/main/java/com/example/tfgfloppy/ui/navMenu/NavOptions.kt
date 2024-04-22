package com.example.navegacionconbotonflotante.composable.navMenu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.navegacionconbotonflotante.composable.constants.Constants

data class NavOptions (
    val icon: ImageVector = Icons.Default.List,
    val title: String = "",
    val route: String = ""
) {
    fun bottomNavigationItems() : List<NavOptions> {
        return listOf(
            NavOptions(
                icon = Icons.Default.Menu,
                title = Constants.NOTES_NAME,
                route = Screens.Notes.route
            ),
            NavOptions(
                icon = Icons.Default.Done,
                title = Constants.TASK_NAME,
                route = Screens.Tasks.route
            )
        )
    }
}