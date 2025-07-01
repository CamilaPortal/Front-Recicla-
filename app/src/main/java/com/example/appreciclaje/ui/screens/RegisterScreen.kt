package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appreciclaje.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        RegisterContent(
            modifier = Modifier.align(Alignment.Center),
            viewModel = viewModel,
            onRegisterSuccess = onRegisterSuccess,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

@Composable
fun RegisterContent(
    modifier: Modifier,
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val dni: String by viewModel.dni.observeAsState("")
    val nombre: String by viewModel.nombre.observeAsState("")
    val apellido: String by viewModel.apellido.observeAsState("")
    val alias: String by viewModel.alias.observeAsState("")
    val telefono: String by viewModel.telefono.observeAsState("")
    val email: String by viewModel.email.observeAsState("")
    val password: String by viewModel.password.observeAsState("")
    val registerEnable: Boolean by viewModel.registerEnable.observeAsState(false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)
    val error: String? by viewModel.error.observeAsState(null)

    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(24.dp))

            DniField(dni) { viewModel.onRegisterFieldsChanged(it, nombre, apellido, alias, telefono, email, password) }
            Spacer(modifier = Modifier.height(8.dp))

            NombreField(nombre) { viewModel.onRegisterFieldsChanged(dni, it, apellido, alias, telefono, email, password) }
            Spacer(modifier = Modifier.height(8.dp))

            ApellidoField(apellido) { viewModel.onRegisterFieldsChanged(dni, nombre, it, alias, telefono, email, password) }
            Spacer(modifier = Modifier.height(8.dp))

            AliasField(alias) { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, it, telefono, email, password) }
            Spacer(modifier = Modifier.height(8.dp))

            TelefonoField(telefono) { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, alias, it, email, password) }
            Spacer(modifier = Modifier.height(8.dp))

            RegisterEmailField(email) { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, alias, telefono, it, password) }
            Spacer(modifier = Modifier.height(8.dp))

            RegisterPasswordField(password) { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, alias, telefono, email, it) }
            Spacer(modifier = Modifier.height(24.dp))

            if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            RegisterButton(registerEnable) {
                coroutineScope.launch {
                    viewModel.onRegisterSelected(onRegisterSuccess)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LoginTextButton(onNavigateToLogin)
        }
    }
}

@Composable
fun DniField(dni: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = dni,
        onValueChange = onTextFieldChanged,
        label = { Text("DNI") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun NombreField(nombre: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = nombre,
        onValueChange = onTextFieldChanged,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun ApellidoField(apellido: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = apellido,
        onValueChange = onTextFieldChanged,
        label = { Text("Apellido") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun AliasField(alias: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = alias,
        onValueChange = onTextFieldChanged,
        label = { Text("Alias") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun TelefonoField(telefono: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = telefono,
        onValueChange = onTextFieldChanged,
        label = { Text("Teléfono") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun RegisterEmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = onTextFieldChanged,
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun RegisterPasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onTextFieldChanged,
        label = { Text("Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4CAE50),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF4CAE50),
            cursorColor = Color(0xFF4CAE50),
            focusedTextColor = Color.Black,
        )
    )
}

@Composable
fun RegisterButton(registerEnable: Boolean, onRegisterSelected: () -> Unit) {
    Button(
        onClick = onRegisterSelected,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAE50),
            disabledContainerColor = Color(0xFF636262),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        enabled = registerEnable
    ) {
        Text("Registrarse",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        )
    }
}

@Composable
fun LoginTextButton(onNavigateToLogin: () -> Unit) {
    TextButton(onClick = onNavigateToLogin) {
        Text("¿Ya tienes cuenta? Inicia Sesión", color = Color(0xFF4CAE50))
    }
}