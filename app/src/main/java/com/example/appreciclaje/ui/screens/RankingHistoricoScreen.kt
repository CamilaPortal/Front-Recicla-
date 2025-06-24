package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.example.appreciclaje.viewmodel.RankingHistoricoViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appreciclaje.data.api.dto.RankingHistoricoItem
import com.example.appreciclaje.viewmodel.RankingHistoricoViewModel.RankingState
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.sp
import com.example.appreciclaje.data.api.dto.MiPosicionHistoricaResponse

@Composable
fun RankingHistoricoScreen(viewModel: RankingHistoricoViewModel = viewModel()) {
    val rankingState by viewModel.rankingState.collectAsState()
    val currentUserAlias by viewModel.currentUserAlias.collectAsState()
    val userPosition by viewModel.userPosition.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        RankingHeader()

        when (rankingState) {
            is RankingState.Loading -> LoadingState()
            is RankingState.Success -> SuccessState(
                ranking = (rankingState as RankingState.Success).ranking,
                currentUserAlias = currentUserAlias,
                userPosition = userPosition
            )
            is RankingState.Error -> ErrorState(
                message = (rankingState as RankingState.Error).message
            )
        }
    }
}

@Composable
private fun RankingHeader() {
    Text(
        text = "Ranking Histórico",
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp),
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Pos.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(50.dp),
            color = Color.Gray
        )
        Text(
            text = "Usuario",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )
        Text(
            text = "Puntos",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.width(80.dp),
            color = Color.Gray
        )
    }

    Divider(color = Color.LightGray)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF4CAE50))
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SuccessState(
    ranking: List<RankingHistoricoItem>,
    currentUserAlias: String,
    userPosition: MiPosicionHistoricaResponse?
) {
    val userInTop10 = ranking.any { it.alias == currentUserAlias }

    LazyColumn {
        items(
            items = ranking,
            key = { it.alias }
        ) { item ->
            RankingItemCard(
                item = item,
                currentUserAlias = currentUserAlias
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!userInTop10 && userPosition != null) {
            item {
                UserPositionSection(
                    userPosition = userPosition,
                    currentUserAlias = currentUserAlias
                )
            }
        }
    }
}

@Composable
private fun UserPositionSection(
    userPosition: MiPosicionHistoricaResponse,
    currentUserAlias: String
) {
    Spacer(modifier = Modifier.height(16.dp))
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    Text(
        text = if (userPosition.mi_posicion_historica != null)
            "Tu posición en el ranking"
        else "Aún no tienes posición en el ranking",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    userPosition.mensaje?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    val userPositionItem = RankingHistoricoItem(
        posicion = userPosition.mi_posicion_historica ?: 0,
        alias = currentUserAlias,
        nombre = "",
        apellido = "",
        puntos_disponibles = userPosition.mis_puntos_disponibles,
        total_puntos_ganados = userPosition.mis_puntos_historicos,
        total_reciclajes_realizados = userPosition.mis_reciclajes_realizados
    )

    if (userPosition.mi_posicion_historica == null) {
        UserWithoutPositionCard(currentUserAlias = currentUserAlias)
    } else {
        RankingItemCard(
            item = userPositionItem,
            currentUserAlias = currentUserAlias
        )
    }
}

@Composable
private fun UserWithoutPositionCard(currentUserAlias: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color(0xFF2196F3))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(40.dp)
            ) {
                Text(
                    text = "--",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            UserAvatarAndInfo(
                alias = currentUserAlias,
                isCurrentUser = true
            )

            UserStatsColumn(points = 0, recycleCount = 0)
        }
    }
}

@Composable
private fun RowScope.UserAvatarAndInfo(
    alias: String,
    isCurrentUser: Boolean
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(40.dp)
            .background(
                color = getAvatarColor(alias),
                shape = CircleShape
            )
    ) {
        Text(
            text = alias.firstOrNull()?.toString()?.uppercase() ?: "",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }

    Text(
        text = alias,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.weight(1f),
        color = Color.Black
    )

    if (isCurrentUser) {
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF2196F3),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "Tú",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UserStatsColumn(points: Int, recycleCount: Int) {
    Column(horizontalAlignment = Alignment.End) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFF4CAE50),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$points",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAE50)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Recycling,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$recycleCount",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RankingItemCard(
    item: RankingHistoricoItem,
    currentUserAlias: String = ""
) {
    val isCurrentUser = item.alias == currentUserAlias

    val backgroundColor = when {
        isCurrentUser -> Color(0xFFE3F2FD)
        item.posicion == 1 -> Color(0xFFFFF9C4)
        item.posicion == 2 -> Color(0xFFE0E0E0)
        item.posicion == 3 -> Color(0xFFFFCCBC)
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentUser) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        border = if (isCurrentUser) {
            BorderStroke(2.dp, Color(0xFF2196F3))
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            PositionDisplay(position = item.posicion)

            UserAvatarAndInfo(
                alias = item.alias,
                isCurrentUser = isCurrentUser
            )

            UserStatsColumn(
                points = item.total_puntos_ganados,
                recycleCount = item.total_reciclajes_realizados
            )
        }
    }
}

@Composable
private fun PositionDisplay(position: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.width(40.dp)
    ) {
        when(position) {
            1 -> Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = "Primer puesto",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
            2 -> Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = "Segundo puesto",
                tint = Color(0xFFC0C0C0),
                modifier = Modifier.size(28.dp)
            )
            3 -> Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = "Tercer puesto",
                tint = Color(0xFFCD7F32),
                modifier = Modifier.size(24.dp)
            )
            else -> Text(
                text = "$position",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun getAvatarColor(alias: String): Color {
    val colors = listOf(
        Color(0xFF1976D2),
        Color(0xFFE53935),
        Color(0xFF43A047),
        Color(0xFF8E24AA),
        Color(0xFFEF6C00),
        Color(0xFF0097A7)
    )

    val index = alias.sumOf { it.code } % colors.size
    return colors[index]
}