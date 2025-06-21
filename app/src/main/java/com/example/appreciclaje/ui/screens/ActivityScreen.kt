package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.appreciclaje.viewmodel.ActivityViewModel

@Composable
fun ActivityScreen(viewModel: ActivityViewModel = viewModel()) {
    val userMovements by viewModel.userMovements.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

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
            Text(
                text = "Historial de Actividad",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF4CAE50)
                )
            } else if (error != null) {
                Text(
                    text = error ?: "Error desconocido",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                ActivityList(userMovements)
            }
        }
    }
}

@Composable
fun ActivityList(movements: List<UserMovement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            if (movements.isEmpty()) {
                item {
                    Text(
                        text = "No hay movimientos registrados",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                items(movements.size) { index ->
                    val movement = movements[index]
                    ActivityMovementItem(movement)
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

@Composable
fun ActivityMovementItem(movement: UserMovement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
        Text(
            text = if (movement.isPositive) "+${movement.points} puntos" else "-${movement.points} puntos",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (movement.isPositive) Color(0xFF4CAE50) else Color.Red
        )
    }
}