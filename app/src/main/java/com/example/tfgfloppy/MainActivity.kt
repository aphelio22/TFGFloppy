package com.example.tfgfloppy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.navegacionconbotonflotante.composable.navMenu.NavOptions
import com.example.navegacionconbotonflotante.composable.navMenu.Screens
import com.example.navegacionconbotonflotante.composable.screens.noteScreen.MyNoteScreen
import com.example.tfgfloppy.addNote.ui.NoteViewModel
import com.example.tfgfloppy.addTask.ui.MyTaskScreen
import com.example.tfgfloppy.addTask.ui.TaskViewModel
import com.example.tfgfloppy.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var showSheet by remember { mutableStateOf(false) }
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        BottomNavigationBar(taskViewModel, noteViewModel)
                        Button(onClick = {
                            showSheet = true
                        }) {
                            Text(text = "Show BottomSheet")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(taskViewModel: TaskViewModel, noteViewModel: NoteViewModel) {
    var navigationSelectedItem by rememberSaveable {
        mutableStateOf(0)
    }
    val navController = rememberNavController()
    Scaffold(Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavOptions().bottomNavigationItems().forEachIndexed { index, navOptions ->
                    val fontFamilyRobotoBlack = FontFamily(Font(R.font.roboto_regular))
                    NavigationBarItem(
                        selected = index == navigationSelectedItem,
                        label = {
                            Text(navOptions.title, fontFamily = fontFamilyRobotoBlack, fontSize = 16.sp)
                        },
                        icon = {
                            Icon(
                                imageVector = navOptions.icon,
                                contentDescription = navOptions.title
                            )
                        },
                        onClick = {
                            navigationSelectedItem = index
                            navController.navigate(navOptions.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
        //showBottomSheet = true
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Notes.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Screens.Notes.route) {
                MyNoteScreen(navController, LocalContext.current, noteViewModel)
            }
            composable(Screens.Tasks.route) {
                MyTaskScreen(taskViewModel)
            }
        }
    }
}