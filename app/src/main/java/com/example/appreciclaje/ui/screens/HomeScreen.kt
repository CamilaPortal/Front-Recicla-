package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appreciclaje.data.api.dto.UserMovement
import com.example.appreciclaje.viewmodel.HomeViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val userName by viewModel.userName.observeAsState("")
    val userPoints by viewModel.userPoints.observeAsState(0)
    val userMovements by viewModel.userMovements.observeAsState(emptyList())
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
            Greeting(userName, onLogoutClick = { viewModel.logout() })
            Spacer(modifier = Modifier.height(16.dp))
            PointsCard(userPoints)
            Spacer(modifier = Modifier.height(16.dp))
            MovementsCard(userMovements)
        }
    }
}

@Composable
fun Greeting(userName: String, onLogoutClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .background(Color(0xFF4CAE50), shape = CircleShape)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Column {
            Text(
                text = "Hola",
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
fun PointsCard(points: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Puntos disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$points pts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAE50)
            )
        }
    }
}

@Composable
fun MovementsCard(movements: List<UserMovement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Últimos movimientos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                if (movements.isEmpty()) {
                    item {
                        Text(
                            text = "No hay movimientos recientes",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(movements.size) { index ->
                        val movement = movements[index]
                        MovementItem(movement)
                        if (index < movements.size - 1) {
                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovementItem(movement: UserMovement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = movement.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = movement.date,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Text(
            text = if (movement.isPositive) "+${movement.points}" else "-${movement.points}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (movement.isPositive) Color(0xFF4CAE50) else Color.Red
        )
    }
}