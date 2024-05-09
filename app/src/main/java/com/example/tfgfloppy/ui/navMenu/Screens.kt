package com.example.tfgfloppy.ui.navMenu

import com.example.tfgfloppy.constants.Constants

sealed class Screens(val route: String) {
    data object Notes: Screens(Constants.NOTES_ROUTE)
    data object Tasks: Screens(Constants.TASK_ROUTE)
}