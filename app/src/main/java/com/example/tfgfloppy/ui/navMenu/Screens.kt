package com.example.navegacionconbotonflotante.composable.navMenu

import com.example.navegacionconbotonflotante.composable.constants.Constants

sealed class Screens(val route: String) {
    object Notes: Screens(Constants.NOTES_ROUTE)
    object Tasks: Screens(Constants.TASK_ROUTE)
}