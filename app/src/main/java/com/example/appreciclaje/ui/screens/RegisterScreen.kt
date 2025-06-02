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
            Text("Crear Cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = dni,
                onValueChange = { viewModel.onRegisterFieldsChanged(it, nombre, apellido, alias, telefono, email, password) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { viewModel.onRegisterFieldsChanged(dni, it, apellido, alias, telefono, email, password) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { viewModel.onRegisterFieldsChanged(dni, nombre, it, alias, telefono, email, password) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = alias,
                onValueChange = { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, it, telefono, email, password) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, alias, it, email, password) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, alias, telefono, it, password) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onRegisterFieldsChanged(dni, nombre, apellido, alias, telefono, email, it) },
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
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.onRegisterSelected(onRegisterSuccess)
                    }
                },
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
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia Sesión", color = Color(0xFF4CAE50))
            }
        }
    }
}