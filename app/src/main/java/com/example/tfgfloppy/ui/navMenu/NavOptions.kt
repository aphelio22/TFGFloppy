package com.example.tfgfloppy.ui.navMenu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tfgfloppy.constants.Constants

data class NavOptions (
    val icon: ImageVector = Icons.AutoMirrored.Filled.List,
    val title: String = "",
    val route: String = ""
) {
    fun bottomNavigationItems() : List<NavOptions> {
        return listOf(
            NavOptions(
                icon = Icons.Filled.Menu,
                title = Constants.NOTES_NAME,
                route = Screens.Notes.route
            ),
            NavOptions(
                icon = Icons.Filled.Task,
                title = Constants.TASK_NAME,
                route = Screens.Tasks.route
            )
        )
    }
}