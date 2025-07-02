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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadMisCanjes() },
            modifier = Modifier.padding(paddingValues),
        ) {
            if (isLoading && canjes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF4CAE50)
                    )
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Error: $error",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(canjes, key = { it.id }) { canje ->
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
        colors = CardDefaults.cardColors(containerColor = if (canje.is_active) Color.White else Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Icono de canje",
                    tint = Color(0xFF4CAE50),
                    modifier = Modifier.size(40.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = canje.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = canje.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Puntos", style = MaterialTheme.typography.labelMedium, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = puntosText,
                            onValueChange = { puntosText = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF4CAE50),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF4CAE50)
                            )
                        )
                        IconButton(onClick = {
                            puntosText.toIntOrNull()?.let { onPointsChange(canje.id, it) }
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar Puntos", tint = Color(0xFF4CAE50))
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Stock", style = MaterialTheme.typography.labelMedium, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { stockText = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF4CAE50),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF4CAE50)
                            )
                        )
                        IconButton(onClick = {
                            stockText.toIntOrNull()?.let { onStockChange(canje.id, it) }
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar Stock", tint = Color(0xFF4CAE50))
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("Activo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = canje.is_active,
                    onCheckedChange = { onStatusChange(canje.id, it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAE50)
                    )
                )
            }
        }
    }
}