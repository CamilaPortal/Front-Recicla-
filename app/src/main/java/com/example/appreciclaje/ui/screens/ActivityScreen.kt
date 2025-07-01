package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appreciclaje.data.api.dto.UserMovement
import com.example.appreciclaje.viewmodel.ActivityViewModel
import com.example.appreciclaje.viewmodel.MovementFilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navController: NavController,
    viewModel: ActivityViewModel = viewModel()
) {
    val userMovements by viewModel.userMovements.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
    val filterType by viewModel.filterType.observeAsState(MovementFilterType.ALL)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de actividad", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAE50)
                ),
                windowInsets = WindowInsets(top = 0.dp)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            FilterButtons(
                selectedFilter = filterType,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAE50))
                }
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
fun FilterButtons(
    selectedFilter: MovementFilterType,
    onFilterSelected: (MovementFilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterButton(
            text = "Todos",
            isSelected = selectedFilter == MovementFilterType.ALL,
            onClick = { onFilterSelected(MovementFilterType.ALL) },
            modifier = Modifier.weight(1f)
        )
        FilterButton(
            text = "Reciclajes",
            isSelected = selectedFilter == MovementFilterType.RECYCLING,
            onClick = { onFilterSelected(MovementFilterType.RECYCLING) },
            modifier = Modifier.weight(1f)
        )
        FilterButton(
            text = "Canjes",
            isSelected = selectedFilter == MovementFilterType.EXCHANGE,
            onClick = { onFilterSelected(MovementFilterType.EXCHANGE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFF4CAE50)
    val inactiveColor = Color.LightGray
    val activeTextColor = Color.White
    val inactiveTextColor = Color.Black

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) activeColor else inactiveColor
        )
    ) {
        Text(
            text = text,
            color = if (isSelected) activeTextColor else inactiveTextColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
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
                        text = "No hay movimientos para este filtro",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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