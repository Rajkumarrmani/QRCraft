package com.app.qrcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.app.qrcraft.navigation.Destination
import com.app.qrcraft.ui.theme.QRCraftTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QRCraftTheme {

                val backStack = rememberNavBackStack<Destination>(Destination.SplashScreen)

                NavDisplay(
                    backStack = backStack,
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    entryProvider = entryProvider {

                        entry(Destination.SplashScreen) {
                            SplashScreen(
                                homePage = {
                                    backStack.removeLastOrNull()
                                    backStack.add(Destination.Scan)
                                }
                            )
                        }

                        entry<Destination.Scan> {
                            HomeScreen(
                                modifier = Modifier,
                                onQrCodeDetected = {
                                    backStack.add(Destination.ScanDetail(it))
                                },
                                onExit = {
                                    backStack.removeLastOrNull()
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

