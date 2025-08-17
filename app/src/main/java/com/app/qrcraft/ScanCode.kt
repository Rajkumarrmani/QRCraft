package com.app.qrcraft


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.app.qrcraft.ui.ShowRationaleDialog
import com.app.qrcraft.ui.theme.QRCraftTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.delay


@Preview
@Composable
private fun ScanCodePreview() {

    QRCraftTheme {
        ScanCode(
            onQrCodeDetected = { barcode ->

            },
            onExit = {

            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanCode(
    onExit: () -> Unit,
    onQrCodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    if (!hasCameraPermission) {
        ShowRationaleDialog(
            onDismiss = {
                onExit.invoke()
            },
            onConfirm = {
                launcher.launch(Manifest.permission.CAMERA)
            }
        )
    }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
            )
        }
    }

    var barcode by remember { mutableStateOf<String?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var qrCodeDetected by remember { mutableStateOf(false) }

    val scanBoxRect = remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()

                val barcodeScanner = BarcodeScanning.getClient(options)

                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                        ContextCompat.getMainExecutor(ctx)
                    ) { result ->


                        val barcodeResults = result?.getValue(barcodeScanner)
                        if (!barcodeResults.isNullOrEmpty() && scanBoxRect.value != null) {
                            val canvasRect = Rect(
                                scanBoxRect.value!!.left.toInt(),
                                scanBoxRect.value!!.top.toInt(),
                                scanBoxRect.value!!.right.toInt(),
                                scanBoxRect.value!!.bottom.toInt()
                            )
                            barcodeResults.forEach { scannedBarcode ->
                                scannedBarcode.boundingBox?.let { detectedRect ->
                                    if (canvasRect.contains(detectedRect)) {
                                        barcode = scannedBarcode.rawValue
                                        qrCodeDetected = true
                                    }
                                }
                            }
                        }
                    }
                )

                cameraController.bindToLifecycle(lifecycleOwner)
                this.controller = cameraController
            }
        }
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val sizePx = minOf(coordinates.size.width, coordinates.size.height) * 0.8f
                val width = sizePx
                val height = sizePx
                val topLeftX = (coordinates.size.width - width) / 2f
                val topLeftY = (coordinates.size.height - height) / 2f
                scanBoxRect.value = androidx.compose.ui.geometry.Rect(
                    topLeftX,
                    topLeftY,
                    topLeftX + width,
                    topLeftY + height
                )
            }) {
        scanBoxRect.value?.let { rect ->
            val cornerLength = 100f
            val strokeWidth = 8f
            val cornerRadius = 30f

            val path = Path().apply {
                moveTo(rect.topLeft.x, rect.topLeft.y + cornerRadius)
                quadraticBezierTo(
                    rect.topLeft.x,
                    rect.topLeft.y,
                    rect.topLeft.x + cornerRadius,
                    rect.topLeft.y
                )
                lineTo(rect.topLeft.x + cornerLength, rect.topLeft.y)

                moveTo(rect.topLeft.x + cornerRadius, rect.topLeft.y)
                quadraticBezierTo(
                    rect.topLeft.x,
                    rect.topLeft.y,
                    rect.topLeft.x,
                    rect.topLeft.y + cornerRadius
                )
                lineTo(rect.topLeft.x, rect.topLeft.y + cornerLength)

                // Top-right corner
                moveTo(rect.topRight.x - cornerRadius, rect.topRight.y)
                quadraticBezierTo(
                    rect.topRight.x,
                    rect.topRight.y,
                    rect.topRight.x,
                    rect.topRight.y + cornerRadius
                )
                lineTo(rect.topRight.x, rect.topRight.y + cornerLength)

                moveTo(rect.topRight.x, rect.topRight.y + cornerRadius)
                quadraticBezierTo(
                    rect.topRight.x,
                    rect.topRight.y,
                    rect.topRight.x - cornerRadius,
                    rect.topRight.y
                )
                lineTo(rect.topRight.x - cornerLength, rect.topRight.y)

                // Bottom-left corner
                moveTo(rect.bottomLeft.x, rect.bottomLeft.y - cornerRadius)
                quadraticBezierTo(
                    rect.bottomLeft.x,
                    rect.bottomLeft.y,
                    rect.bottomLeft.x + cornerRadius,
                    rect.bottomLeft.y
                )
                lineTo(rect.bottomLeft.x + cornerLength, rect.bottomLeft.y)

                moveTo(rect.bottomLeft.x + cornerRadius, rect.bottomLeft.y)
                quadraticBezierTo(
                    rect.bottomLeft.x,
                    rect.bottomLeft.y,
                    rect.bottomLeft.x,
                    rect.bottomLeft.y - cornerRadius
                )
                lineTo(rect.bottomLeft.x, rect.bottomLeft.y - cornerLength)

                // Bottom-right corner
                moveTo(rect.bottomRight.x - cornerRadius, rect.bottomRight.y)
                quadraticBezierTo(
                    rect.bottomRight.x,
                    rect.bottomRight.y,
                    rect.bottomRight.x,
                    rect.bottomRight.y - cornerRadius
                )
                lineTo(rect.bottomRight.x, rect.bottomRight.y - cornerLength)

                moveTo(rect.bottomRight.x, rect.bottomRight.y - cornerRadius)
                quadraticBezierTo(
                    rect.bottomRight.x,
                    rect.bottomRight.y,
                    rect.bottomRight.x - cornerRadius,
                    rect.bottomRight.y
                )
                lineTo(rect.bottomRight.x - cornerLength, rect.bottomRight.y)
            }
            drawPath(path = path, color = Color.Yellow, style = Stroke(width = strokeWidth))

            val outerPath = Path().apply {
                addRect(
                    androidx.compose.ui.geometry.Rect(
                        0f,
                        0f,
                        size.width,
                        size.height
                    )
                )
                addRect(rect)
                fillType = PathFillType.EvenOdd
            }
            clipPath(outerPath) {
                drawRect(Color.Gray.copy(alpha = 0.5f))
            }
        }
    }
    if (qrCodeDetected) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 4.dp
            )

            LaunchedEffect(Unit) {

                delay(100)
                onQrCodeDetected(barcode ?: "")
            }

        }
    }
}