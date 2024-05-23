package com.example.tfgfloppy.activities

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
import androidx.compose.ui.res.stringResource
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
import com.example.tfgfloppy.R
import com.example.tfgfloppy.ui.screen.note.MyNoteScreen
import com.example.tfgfloppy.addNote.viewmodel.NoteViewModel
import com.example.tfgfloppy.ui.screen.task.HorizontalLine
import com.example.tfgfloppy.ui.screen.task.MyTaskScreen
import com.example.tfgfloppy.addTask.viewmodel.TaskViewModel
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
                            LocalContext.current
                        )
                        LogOutDialog(
                            show = showLogOutDialog,
                            onDismiss = { authViewModel.dialogClose() },
                            fontFamily = fontFamilyRobotoRegular,
                            authViewModel = authViewModel,
                            LocalContext.current
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

    //Scaffold es un componente de diseño que proporciona una estructura de diseño básico,
    //como una barra de navegación inferior.
    Scaffold(
        modifier = Modifier.fillMaxSize(), // El Scaffold llena el tamaño disponible.
        bottomBar = {
            NavigationBar {
                //Se itera sobre los elementos de navegación definidos en `NavOptions().bottomNavigationItems()`.
                NavOptions().bottomNavigationItems().forEachIndexed { index, navOptions ->
                    val fontFamilyRobotoBlack = FontFamily(Font(R.font.roboto_regular))

                    //Definición de cada elemento de la barra de navegación.
                    NavigationBarItem(
                        selected = index == navigationSelectedItem, //Indica si el elemento está seleccionado.
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
                            navigationSelectedItem = index //Actualiza el elemento seleccionado.
                            navController.navigate(navOptions.route) { //Navega a la ruta correspondiente.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true //Guarda el estado.
                                }
                                launchSingleTop = true //Evita múltiples instancias de la misma pantalla.
                                restoreState = true //Restaura el estado si es posible.
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        //NavHost define el contenedor de navegación y las rutas disponibles.
        NavHost(
            navController = navController,
            startDestination = Screens.Notes.route,
            modifier = Modifier.padding(paddingValues = paddingValues) //Asegura que el contenido no quede oculto tras la barra de navegación.
        ) {
            //Definición de la pantalla de notas.
            composable(Screens.Notes.route, enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) //Transición de entrada deslizante.
            }, exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) //Transición de salida deslizante.
            }, popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) //Transición de salida deslizante.
            }) {
                //Contenido de la pantalla de notas.
                MyNoteScreen(LocalContext.current, noteViewModel, authViewModel = authViewModel)
            }

            //Definición de la pantalla de tareas.
            composable(Screens.Tasks.route, enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) //Transición de entrada deslizante.
            }, popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) //Transición de salida deslizante.
            }) {
                //Contenido de la pantalla de tareas.
                MyTaskScreen(LocalContext.current, taskViewModel)
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
    context: Context
) {
    var showPassword by remember { mutableStateOf(false) }
    var loginResult by remember(authViewModel.loginResult) {
        mutableStateOf<Result<FirebaseUser?>?>(null)
    }

    //Observa los resultados de la autenticación y actualiza el estado de loginResult.
    LaunchedEffect(Unit) {
        authViewModel.loginResult.collect { result ->
            loginResult = result
            if (result.isFailure) {
                Toast.makeText(
                    context,
                    context.getString(R.string.noValidInformation_MainActivityLoginDialog),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (result.isSuccess) {
                onDismiss()
                Toast.makeText(
                    context,
                    context.getString(R.string.validCredentials_MainActivityLoginDialog),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //Muestra el diálogo de inicio de sesión si es necesario.
    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
            Column( //Contenido del diálogo.
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.ManageAccounts,
                    contentDescription = stringResource(R.string.accountManagementLogo_MainActivityLoginDialog),
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = stringResource(R.string.loginTitle_MainActivityLoginDialog),
                    fontFamily = fontFamily,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                OutlinedTextField(
                    value = loginEtEmail,
                    onValueChange = { onValueChangedEmail(it) },
                    label = { Text(text = stringResource(R.string.emailHint_MainActivityLoginDialog)) })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                //En el campo de contraseña se muestra un icono para mostrar la contraseña.
                OutlinedTextField(
                    value = loginEtPassword,
                    onValueChange = { onValueChangedPassword(it) },
                    label = { Text(text = stringResource(R.string.passwordHint_MainActivityLoginDialog)) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(R.string.togglePasswordVisibility_MainActivityLoginDialog)
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
                    Text(text = stringResource(R.string.loginButtonText_MainActivityLoginDialog))
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(
                    text = stringResource(R.string.loginToRegister_MainActivityLoginDialog),
                    fontFamily = fontFamily,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(top = 3.dp))
                TextButton(onClick = {
                    //Se descarta el diálogo actual y se abre el diálogo de registro.
                    onDismiss()
                    authViewModel.onShowDialogToSignUp()
                }) {
                    Text(
                        text = stringResource(R.string.loginToRegisterText_MainActivityLoginDialog),
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )
                }
                TextButton(onClick = {
                    //Se descarta el diálogo actual y se abre el diálogo de restablecimiento de contraseña.
                    onDismiss()
                    authViewModel.onShowDialogToResetPassword()
                }) {
                    Text(
                        text = stringResource(R.string.forgotPassText_MainActivityLoginDialog),
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

    //Muestra el diálogo de registro si es necesario.
    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
            Column( //Componentes del diálogo.
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.ManageAccounts,
                    contentDescription = stringResource(R.string.accountManagementLogo_MainActivitySignUpDialog),
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = stringResource(R.string.registerTitle_MainActivitySignUpDialog),
                    fontFamily = fontFamily,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                OutlinedTextField(
                    value = signUpEtEmail,
                    onValueChange = { onValueChangedEmail(it) },
                    label = { Text(text = stringResource(R.string.emailHint_MainActivitySignUpDialog)) })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                //En el campo de contraseña se muestra un icono para mostrar la contraseña.
                OutlinedTextField(
                    value = signUpEtPassword,
                    onValueChange = {
                        onValueChangedPassword(it)
                        enableButton = it.length > 6
                    },
                    label = { Text(text = stringResource(R.string.passwordHint_MainActivitySignUpDialog)) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(R.string.togglePasswordVisibility_MainActivitySignUpDialog)
                            )
                        }
                    })
                Spacer(modifier = Modifier.padding(top = 10.dp))
                //En el campo de repetir contraseña se muestra un icono para mostrar la contraseña.
                OutlinedTextField(
                    value = signUpEtRepeatPassword,
                    onValueChange = {
                        onValueChangedRepeatPassword(it)
                        enableButton = it.length > 6
                    },
                    label = { Text(text = stringResource(R.string.repeatPasswordHint_MainActivitySignUpDialog)) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = stringResource(R.string.togglePasswordVisibility_MainActivitySignUpDialog)
                            )
                        }
                    })
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(
                    text = stringResource(R.string.passwordDigits_MainActivitySignUpDialog),
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
                Spacer(modifier = Modifier.padding(top = 15.dp))
                //En el botón se maneja la posibilidad de que exista algún tipo de error.
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
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.succesfulSignUp_MainActivitySignUpDialog),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.passwordsDontMatch_MainActivitySignUpDialog),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.notValidEmail_MainActivitySignUpDialog),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    enabled = enableButton
                ) {
                    Text(text = stringResource(R.string.registerTextButton_MainActivitySignUpDialog))
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(
                    text = stringResource(R.string.accountText_MainActivitySignUpDialog),
                    fontFamily = fontFamily,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(top = 3.dp))
                TextButton(onClick = {
                    //Se descarta el diálogo actual y se abre el diálogo de inicio de sesión.
                    onDismiss()
                    authViewModel.onShowDialogToLogin()
                }) {
                    Text(
                        text = stringResource(R.string.signUpToLogin_MainActivitySignUpDialog),
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
fun LogOutDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    fontFamily: FontFamily,
    authViewModel: AuthViewModel,
    context: Context
) {

    //Muestra el diálogo de cerrar sesión si es necesario.
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
                    contentDescription = stringResource(R.string.accountManagementLogo_MainActivityLogOutDialog),
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = authViewModel.currentUser.value?.email.toString(), //Se muestra el email del usuario.
                    fontFamily = fontFamily,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(
                    text = stringResource(R.string.logOutText_MainActivityLogOutDialog),
                    fontFamily = fontFamily,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(
                    text = stringResource(R.string.logOutAdviseText_MainActivityLogOutDialog),
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(
                    onClick = {
                        //Cierra sesión y se descarta el diálogo.
                        authViewModel.logOut()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.acceptLogOutButtonText_MainActivityLogOutDialog))
                }
                Spacer(modifier = Modifier.padding(top = 5.dp))
                //Se manejala posibilidad de que no haya internet.
                if (!authViewModel.isInternetAvailable(context = context)) {
                    Text(
                        text = stringResource(R.string.noInternet_MainActivityLogOutDialog),
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.noInternetDescription_MainActivityLogOutDialog),
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center
                    )
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

    //Muestra el diálogo de restaurar contraseña si es necesario.
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
                    contentDescription = stringResource(R.string.accountManagementLogo_MainActivityResetPassDialog),
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = stringResource(R.string.resetPasswordTitle_MainActivityResetPassDialog),
                    fontFamily = fontFamily,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(
                    text = stringResource(R.string.resetPassText_MainActivityResetPassDialog),
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                OutlinedTextField(
                    value = resetPasswordEtEmail,
                    onValueChange = { onValueChangedEmail(it) },
                    label = { Text(text = stringResource(R.string.emailHint_MainActivityResetPassDialog)) })
                Spacer(modifier = Modifier.padding(top = 20.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Button(
                    onClick = {
                        //Si el formato de email no es válido salta un error.
                        if (resetPasswordEtEmail.isNotEmpty() && resetPasswordEtEmail.contains("@" + "gmail.com")) {
                            authViewModel.resetPassword(resetPasswordEtEmail)
                            onDismiss()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.emailNotValid_MainActivityResetPassDialog),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.sendEmail_MainActivityResetPassDialog))
                }
            }
        }
    }
}





