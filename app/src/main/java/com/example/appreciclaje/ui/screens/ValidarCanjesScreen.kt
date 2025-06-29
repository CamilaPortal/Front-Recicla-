package com.example.appreciclaje.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appreciclaje.data.api.dto.ConfirmarEntregaResponse
import com.example.appreciclaje.data.api.dto.ValidarQrResponse
import com.example.appreciclaje.viewmodel.ValidarCanjeState
import com.example.appreciclaje.viewmodel.ValidarCanjesViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun ValidarCanjesScreen(
    navController: NavController,
    viewModel: ValidarCanjesViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )
    var manualCode by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validar Canje", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAE50)),
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
            if (hasCameraPermission) {
                when (val currentState = state) {
                    is ValidarCanjeState.Idle, is ValidarCanjeState.Scanning -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CameraView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                cameraController = cameraController,
                                lifecycleOwner = lifecycleOwner,
                                onCodeScanned = viewModel::onCodeScanned
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = manualCode,
                                onValueChange = { manualCode = it },
                                label = { Text("O ingrese el código manualmente") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAE50),
                                    unfocusedBorderColor = Color.Gray,
                                    cursorColor = Color(0xFF4CAE50)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.onCodeScanned(manualCode) },
                                enabled = manualCode.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAE50),
                                    disabledContainerColor = Color.Gray
                                )
                            ) {
                                Text("Validar Código", color = Color.White)
                            }
                        }
                    }
                    is ValidarCanjeState.Validating, is ValidarCanjeState.Confirming -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is ValidarCanjeState.Success -> {
                        ValidationSuccessView(data = currentState.data, onConfirm = viewModel::confirmarEntrega, onScanAnother = viewModel::resetState)
                    }
                    is ValidarCanjeState.ConfirmationSuccess -> {
                        ConfirmationSuccessView(data = currentState.data, onFinish = viewModel::resetState)
                    }
                    is ValidarCanjeState.Error -> {
                        ErrorView(message = currentState.message, onRetry = viewModel::resetState)
                    }
                }
            } else {
                PermissionRequestView { permissionLauncher.launch(Manifest.permission.CAMERA) }
            }
        }
    }
}

@ExperimentalGetImage
@Composable
private fun CameraView(
    modifier: Modifier = Modifier,
    cameraController: LifecycleCameraController,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(true) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
            val scanner = BarcodeScanning.getClient(options)
            cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                try {
                    imageProxy.image?.let { mediaImage ->
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty() && isScanning) {
                                    barcodes.first().rawValue?.let { code ->
                                        onCodeScanned(code)
                                        isScanning = false
                                    }
                                }
                                imageProxy.close()
                            }
                            .addOnFailureListener {
                                Log.e("CameraView", "Error en el escaneo", it)
                                imageProxy.close()
                            }
                    } ?: imageProxy.close()
                } catch (e: Exception) {
                    Log.e("CameraView", "Error en el análisis", e)
                    imageProxy.close()
                }
            }
        } else {
            cameraController.clearImageAnalysisAnalyzer()
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ValidationSuccessView(data: ValidarQrResponse, onConfirm: () -> Unit, onScanAnother: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAE50), modifier = Modifier.size(64.dp))
        Text(data.message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = Color.Black)

        data.canje_info?.let {
            InfoCard(title = "Detalles del Canje") {
                ValidationInfoRow("Premio:", it.premio)
                ValidationInfoRow("Puntos:", it.puntos_usados.toString())
                ValidationInfoRow("Vencimiento:", it.fecha_vencimiento)
            }
        }
        data.usuario_info?.let {
            InfoCard(title = "Información del Usuario") {
                ValidationInfoRow("Nombre:", it.nombre)
                ValidationInfoRow("DNI:", it.dni.toString())
            }
        }
        data.instrucciones?.let {
            InfoCard(title = "Instrucciones") {
                it.forEach { instruction ->
                    Text(
                        text = "• $instruction",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAE50))
        ) {
            Text("Confirmar Entrega", color = Color.White)
        }
        Button(onClick = onScanAnother, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
            Text("Escanear Otro", color = Color.White)
        }
    }
}

@Composable
private fun ConfirmationSuccessView(data: ConfirmarEntregaResponse, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAE50), modifier = Modifier.size(64.dp))
        Text(data.message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = Color.Black)

        data.entrega_confirmada?.let {
            InfoCard(title = "Resumen de Entrega") {
                ValidationInfoRow("Premio:", it.premio)
                ValidationInfoRow("Usuario:", it.usuario)
                ValidationInfoRow("DNI:", it.dni_usuario.toString())
                ValidationInfoRow("Puntos Canjeados:", it.puntos_canjeados.toString())
                ValidationInfoRow("Fecha de Entrega:", it.fecha_entrega)
            }
        }
        data.qr_status?.let {
            Text("Estado del QR: $it", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAE50))
        ) {
            Text("Finalizar", color = Color.White)
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ValidationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f),
            color = Color.Black
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            color = Color.DarkGray
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Error", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = Color.Black)
        Text(message, textAlign = TextAlign.Center, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAE50))
        ) {
            Text("Intentar de Nuevo", color = Color.White)
        }
    }
}

@Composable
private fun PermissionRequestView(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Se requiere permiso de cámara para validar canjes.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequest) {
            Text("Solicitar Permiso")
        }
    }
}