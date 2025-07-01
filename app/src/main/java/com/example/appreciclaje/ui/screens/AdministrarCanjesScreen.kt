package com.example.appreciclaje.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appreciclaje.data.api.dto.CanjesEmpresaResponse
import com.example.appreciclaje.viewmodel.AdministrarCanjesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrarCanjesScreen(
    navController: NavController,
    viewModel: AdministrarCanjesViewModel = viewModel()
) {
    val canjes by viewModel.canjes.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.updateSuccess.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    if (showDialog) {
        CrearCanjeDialog(
            onDismiss = { showDialog = false },
            onCreate = { nombre, descripcion, puntos, stock ->
                viewModel.crearCanje(nombre, descripcion, puntos, stock)
                showDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Administrar Canjes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAE50)),
                windowInsets = WindowInsets(top = 0.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF4CAE50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Canje", tint = Color.White)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF4CAE50)
                )
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(canjes) { canje ->
                        MiCanjeCard(
                            canje = canje,
                            onPointsChange = viewModel::actualizarPuntos,
                            onStatusChange = viewModel::actualizarEstado,
                            onStockChange = viewModel::actualizarStock
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CrearCanjeDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Int, Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var puntos by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nuevo Canje") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )
                OutlinedTextField(
                    value = puntos,
                    onValueChange = { puntos = it.filter { char -> char.isDigit() } },
                    label = { Text("Puntos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it.filter { char -> char.isDigit() } },
                    label = { Text("Stock Inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Atención: El nombre y la descripción no se podrán editar una vez creado el canje.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                if (isError) {
                    Text("Por favor, complete todos los campos.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val puntosInt = puntos.toIntOrNull()
                    val stockInt = stock.toIntOrNull()
                    if (nombre.isNotBlank() && descripcion.isNotBlank() && puntosInt != null && stockInt != null) {
                        onCreate(nombre, descripcion, puntosInt, stockInt)
                        isError = false
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAE50))
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun MiCanjeCard(
    canje: CanjesEmpresaResponse,
    onPointsChange: (Int, Int) -> Unit,
    onStatusChange: (Int, Boolean) -> Unit,
    onStockChange: (Int, Int) -> Unit
) {
    var puntosText by remember(canje.puntos) { mutableStateOf(canje.puntos.toString()) }
    var stockText by remember(canje.stock_actual) { mutableStateOf(canje.stock_actual.toString()) }
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (canje.is_active) Color(0xFFF0F0F0) else Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Icono de canje",
                    tint = Color(0xFF4CAE50),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = canje.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = canje.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = puntosText,
                        onValueChange = { puntosText = it.filter { char -> char.isDigit() } },
                        label = { Text("Puntos") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.width(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAE50),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF4CAE50),
                            focusedLabelColor = Color(0xFF4CAE50),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            val puntosInt = puntosText.toIntOrNull()
                            if (puntosInt != null && puntosInt != canje.puntos) {
                                onPointsChange(canje.id, puntosInt)
                            }
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.height(56.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAE50))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirmar Puntos", tint = Color.White)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (canje.is_active) "Activo" else "Inactivo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (canje.is_active) Color(0xFF4CAE50) else Color.Red
                    )
                    Switch(
                        checked = canje.is_active,
                        onCheckedChange = { onStatusChange(canje.id, it) },
                        enabled = canje.stock_actual > 0,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAE50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray,
                            disabledCheckedThumbColor = Color.White.copy(alpha = 0.8f),
                            disabledCheckedTrackColor = Color(0xFF4CAE50).copy(alpha = 0.5f),
                            disabledUncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                            disabledUncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it.filter { char -> char.isDigit() } },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.width(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAE50),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF4CAE50),
                        focusedLabelColor = Color(0xFF4CAE50),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true
                )
                Button(
                    onClick = {
                        val stockInt = stockText.toIntOrNull()
                        if (stockInt != null && stockInt != canje.stock_actual) {
                            onStockChange(canje.id, stockInt)
                        }
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.height(56.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAE50))
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirmar Stock", tint = Color.White)
                }
            }
        }
    }
}