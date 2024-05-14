package com.example.tfgfloppy

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tfgfloppy.addNote.ui.MyNoteScreen
import com.example.tfgfloppy.addNote.ui.NoteViewModel
import com.example.tfgfloppy.addTask.ui.HorizontalLine
import com.example.tfgfloppy.addTask.ui.MyTaskScreen
import com.example.tfgfloppy.addTask.ui.TaskViewModel
import com.example.tfgfloppy.firebase.viewmodel.AuthViewModel
import com.example.tfgfloppy.ui.navMenu.NavOptions
import com.example.tfgfloppy.ui.navMenu.Screens
import com.example.tfgfloppy.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            var showSheet by remember { mutableStateOf(false) }
            val showLoginDialog: Boolean by authViewModel.showDialogToLogin.observeAsState(false)
            val showLogOutDialog: Boolean by authViewModel.showDialogToLogOut.observeAsState(false)
            val showSignUpDialog: Boolean by authViewModel.showDialogToSignUp.observeAsState(false)
            val showResetPasswordDialog: Boolean by authViewModel.showDialogToResetPassword.observeAsState(
                false
            )
            val fontFamilyRobotoRegular = FontFamily(Font(R.font.roboto_regular))
            var loginEtEmail by remember { mutableStateOf("") }
            var loginEtPassword by remember { mutableStateOf("") }
            var signUpEtEmail by remember { mutableStateOf("") }
            var signUpEtRepeatPassword by remember { mutableStateOf("") }
            var signUpEtPassword by remember { mutableStateOf("") }
            var resetPasswordEtEmail by remember { mutableStateOf("") }

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        BottomNavigationBar(taskViewModel, noteViewModel, authViewModel)
                        Button(onClick = {
                            showSheet = true
                        }) {
                            Text(text = "Show BottomSheet")
                        }
                        LoginDialog(
                            show = showLoginDialog,
                            onDismiss = { authViewModel.dialogClose() },
                            fontFamily = fontFamilyRobotoRegular,
                            authViewModel = authViewModel,
                            loginEtEmail = loginEtEmail,
                            loginEtPassword = loginEtPassword,
                            onValueChangedEmail = { loginEtEmail = it },
                            onValueChangedPassword = { loginEtPassword = it },
                            noteViewModel = noteViewModel,
                            LocalContext.current
                        )
                        LogOutDialog(
                            show = showLogOutDialog,
                            onDismiss = { authViewModel.dialogClose() },
                            fontFamily = fontFamilyRobotoRegular,
                            authViewModel = authViewModel
                        )
                        SignUpDialog(
                            show = showSignUpDialog,
                            onDismiss = { authViewModel.dialogClose() },
                            fontFamily = fontFamilyRobotoRegular,
                            authViewModel = authViewModel,
                            signUpEtEmail = signUpEtEmail,
                            signUpEtRepeatPassword = signUpEtRepeatPassword,
                            signUpEtPassword = signUpEtPassword,
                            onValueChangedEmail = { signUpEtEmail = it },
                            onValueChangedRepeatPassword = { signUpEtRepeatPassword = it },
                            onValueChangedPassword = { signUpEtPassword = it },
                            LocalContext.current
                        )
                        ResetPasswordDialog(
                            show = showResetPasswordDialog,
                            onDismiss = { authViewModel.dialogClose() },
                            fontFamily = fontFamilyRobotoRegular,
                            authViewModel = authViewModel,
                            resetPasswordEtEmail = resetPasswordEtEmail,
                            onValueChangedEmail = { resetPasswordEtEmail = it },
                            LocalContext.current
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    taskViewModel: TaskViewModel,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel
) {
    var navigationSelectedItem by rememberSaveable {
        mutableIntStateOf(0)
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
                            Text(
                                navOptions.title,
                                fontFamily = fontFamilyRobotoBlack,
                                fontSize = 16.sp
                            )
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
            composable(Screens.Notes.route, enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            }, exitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }, popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }) {
                MyNoteScreen(LocalContext.current, noteViewModel, authViewModel = authViewModel)
            }
            composable(Screens.Tasks.route, enterTransition = {
                slideInHorizontally(initialOffsetX = { it })
            }, popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it })
            }) {
                MyTaskScreen(taskViewModel, authViewModel = authViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    fontFamily: FontFamily,
    authViewModel: AuthViewModel,
    loginEtEmail: String,
    loginEtPassword: String,
    onValueChangedEmail: (String) -> Unit,
    onValueChangedPassword: (String) -> Unit,
    noteViewModel: NoteViewModel,
    context: Context
) {
    var showPassword by remember { mutableStateOf(false) }
    var loginResult by remember(authViewModel.loginResult) {
        mutableStateOf<Result<FirebaseUser?>?>(null)
    }


    LaunchedEffect(Unit) {
        authViewModel.loginResult.collect { result ->
            loginResult = result
            if (result.isFailure) {
                Toast.makeText(
                    context,
                    "Información de inicio de sesión no válida",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (result.isSuccess) {
                onDismiss()
                Toast.makeText(context, "¡Inicio correcto!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.ManageAccounts,
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(text = "Inicio de sesión", fontFamily = fontFamily, fontSize = 24.sp)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                OutlinedTextField(
                    value = loginEtEmail,
                    onValueChange = { onValueChangedEmail(it) },
                    label = { Text(text = "Correo electrónico") })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = loginEtPassword,
                    onValueChange = { onValueChangedPassword(it) },
                    label = { Text(text = "Contraseña") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Button(
                    onClick = {
                        authViewModel.loginWithFirebase(loginEtEmail, loginEtPassword)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Iniciar sesión")
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(text = "¿No tienes cuenta?", fontFamily = fontFamily, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(top = 3.dp))
                TextButton(onClick = {
                    onDismiss()
                    authViewModel.onShowDialogToSignUp()
                }) {
                    Text(text = "¡Regístrate!", fontFamily = fontFamily, fontSize = 16.sp)
                }
                TextButton(onClick = {
                    onDismiss()
                    authViewModel.onShowDialogToResetPassword()
                }) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    fontFamily: FontFamily,
    authViewModel: AuthViewModel,
    signUpEtEmail: String,
    signUpEtRepeatPassword: String,
    signUpEtPassword: String,
    onValueChangedEmail: (String) -> Unit,
    onValueChangedRepeatPassword: (String) -> Unit,
    onValueChangedPassword: (String) -> Unit,
    context: Context
) {
    var showPassword by remember { mutableStateOf(false) }
    var enableButton by remember { mutableStateOf(false) }
//    var loginResult by remember(noteViewModel.loginResult) {
//        mutableStateOf<Result<FirebaseUser?>?>(null)
//    }
//    val currentUser by noteViewModel.currentUser.observeAsState()
//
//    LaunchedEffect(Unit) {
//        noteViewModel.loginResult.collect { result ->
//            loginResult = result
//            if (result.isSuccess) {
//
//                onDismiss()
//            } else {
//                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.ManageAccounts,
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(text = "Registrarse", fontFamily = fontFamily, fontSize = 24.sp)
                Spacer(modifier = Modifier.padding(top = 20.dp))
                OutlinedTextField(
                    value = signUpEtEmail,
                    onValueChange = { onValueChangedEmail(it) },
                    label = { Text(text = "Correo electrónico") })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Spacer(modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = signUpEtPassword,
                    onValueChange = {
                        onValueChangedPassword(it)
                        enableButton = it.length >= 6
                    },
                    label = { Text(text = "Contraseña") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = signUpEtRepeatPassword,
                    onValueChange = {
                        onValueChangedRepeatPassword(it)
                        enableButton = it.length >= 6
                    },
                    label = { Text(text = "Repite la contraseña") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    })
                Spacer(modifier = Modifier.padding(top = 3.dp))
                Text(
                    text = "La contraseña debe ser mayor de 6 dígitos.",
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Button(
                    onClick = {
                        if (signUpEtEmail.isNotEmpty() && signUpEtEmail.contains("@" + "gmail.com")) {
                            if (signUpEtPassword == signUpEtRepeatPassword) {
                                authViewModel.signUp(
                                    signUpEtEmail,
                                    signUpEtPassword,
                                    signUpEtRepeatPassword
                                )
                                onDismiss()
                                Toast.makeText(context, "¡Registro correcto!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Las contraseñas no coinciden",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Email no válido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    enabled = enableButton
                ) {
                    Text(text = "Registrarse")
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(text = "¿Ya tienes cuenta?", fontFamily = fontFamily, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(top = 3.dp))
                TextButton(onClick = {
                    onDismiss()
                    authViewModel.onShowDialogToLogin()
                }) {
                    Text(text = "¡Iniciar sesión!", fontFamily = fontFamily, fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOutDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    fontFamily: FontFamily,
    authViewModel: AuthViewModel
) {
    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.ManageAccounts,
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(text = "Sesión iniciada", fontFamily = fontFamily, fontSize = 24.sp)
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(text = "¿Deseas cerrar sesión?", fontFamily = fontFamily, fontSize = 20.sp)
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(
                    text = "Cerrar sesión provocará que tus datos solo se almacenen en local. ¿Estás seguro de esto?",
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(
                    onClick = {
                        authViewModel.logOut()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Aceptar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    fontFamily: FontFamily,
    authViewModel: AuthViewModel,
    resetPasswordEtEmail: String,
    onValueChangedEmail: (String) -> Unit,
    context: Context
) {
    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.ManageAccounts,
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(text = "Recuperar contraseña", fontFamily = fontFamily, fontSize = 24.sp)
                Text(
                    text = "Si no recuerdas tu contraseña puedes recuperarla introduciendo tu correo electrónico. Te enviaremos las instrucciones para restablecer tu contraseña.",
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                OutlinedTextField(
                    value = resetPasswordEtEmail,
                    onValueChange = { onValueChangedEmail(it) },
                    label = { Text(text = "Correo electrónico") })
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(
                    onClick = {
                        if (resetPasswordEtEmail.isNotEmpty() && resetPasswordEtEmail.contains("@" + "gmail.com")) {
                            authViewModel.resetPassword(resetPasswordEtEmail)
                            onDismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Email no válido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Enviar")
                }
            }
        }
    }
}



