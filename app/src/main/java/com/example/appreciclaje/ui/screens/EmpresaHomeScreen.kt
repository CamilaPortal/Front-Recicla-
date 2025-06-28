package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appreciclaje.viewmodel.EmpresaHomeViewModel

@Composable
fun EmpresaHomeScreen(
    viewModel: EmpresaHomeViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateToValidar: () -> Unit,
    onNavigateToAdministrar: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val userName by viewModel.userName.observeAsState("")
    val navigateToLogin by viewModel.navigateToLogin.observeAsState(false)

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            onLogout()
            viewModel.onLoginNavigated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            EmpresaGreeting(
                userName = userName,
                onLogoutClick = { viewModel.logout() },
                onProfileClick = onNavigateToProfile
            )
            Spacer(modifier = Modifier.height(32.dp))
            OptionCard(
                title = "Validar canjes",
                icon = Icons.Default.CheckCircle,
                onClick = onNavigateToValidar
            )
            Spacer(modifier = Modifier.height(16.dp))
            OptionCard(
                title = "Administrar canjes",
                icon = Icons.Default.Edit,
                onClick = onNavigateToAdministrar
            )
        }
    }
}

@Composable
fun EmpresaGreeting(userName: String, onLogoutClick: () -> Unit, onProfileClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() }
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = "Empresa",
            tint = Color(0xFF4CAE50),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "¡Hola!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onLogoutClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar Sesión",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun OptionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF4CAE50),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}