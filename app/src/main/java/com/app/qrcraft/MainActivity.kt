package com.app.qrcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.app.qrcraft.design_system.CustomSnackBar
import com.app.qrcraft.navigation.Destination
import com.app.qrcraft.ui.theme.QRCraftTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QRCraftTheme {
                val backStack = rememberNavBackStack<Destination>(Destination.Scan)
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = {
                                CustomSnackBar(
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        if (backStack == Destination.ScanDetail) {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.onSurface,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        text = "Scan Result",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back Button",
                                            tint = MaterialTheme.colorScheme.surface
                                        )
                                    }
                                }
                            )
                        }
                    }
                ) {

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(it)) {
                        NavDisplay(
                            backStack = backStack,
                            onBack = {
                                backStack.removeLastOrNull()
                            },
                            entryProvider = entryProvider {
                                entry<Destination.Scan> {
                                    HomeScreen(
                                        modifier = Modifier,
                                        onQrCodeDetected = {
                                            backStack.add(Destination.ScanDetail(it))
                                        },
                                        onExit = {
                                            backStack.removeLastOrNull()
                                        },
                                        cameraPermissionGranted = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Camera permission granted"
                                                )
                                            }
                                        }
                                    )
                                }
                                entry<Destination.ScanDetail> { key ->
                                    ScanDetailScreen(
                                        scanData = key.scanData,
                                        onBack = {
                                            backStack.removeLastOrNull()
                                        }
                                    )
                                }
                            }
                        )
                    }
                }


            }
        }
    }
}

