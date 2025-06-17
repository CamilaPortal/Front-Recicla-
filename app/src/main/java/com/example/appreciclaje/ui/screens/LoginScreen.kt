package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appreciclaje.R
import com.example.appreciclaje.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Login(
            Modifier.align(Alignment.Center),
            viewModel,
            onLoginSuccess,
            onNavigateToRegister
        )
    }
}

@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val error: String? by viewModel.error.observeAsState(null)
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(16.dp))
            EmailField(email, {viewModel.onLoginChanged(it, password)})
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChanged(email, it) }
            Spacer(modifier = Modifier.padding(4.dp))
            if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            RegisterText(onNavigateToRegister)
            Spacer(modifier = Modifier.padding(8.dp))
            LoginButton(loginEnable) {
                coroutineScope.launch {
                    viewModel.onLoginSelected(onLoginSuccess)}
            }
        }

    }
}

@Composable
fun LoginButton(loginEnable: Boolean, onLoginSelected:() -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAE50),
            disabledContainerColor = Color(0xFF636262),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ), enabled = loginEnable
    ) {
        Text(text = "Iniciar Sesión",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        )
    }
}

@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        label = { Text("Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else
                Icons.Filled.VisibilityOff

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
fun EmailField(email:String, onTextFieldChanged: (String) -> Unit) {

    OutlinedTextField(
        value = email,
        onValueChange = {onTextFieldChanged(it)},
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
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.icon),
        contentDescription = "Header",
        modifier = modifier)
}

@Composable
fun RegisterText(onNavigateToRegister: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿No tienes cuenta? ",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "Regístrate aquí",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAE50),
            modifier = Modifier.clickable { onNavigateToRegister() }
        )
    }
}