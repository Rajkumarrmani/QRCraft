package com.app.qrcraft

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.app.qrcraft.navigation.ScanData
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onExit: () -> Unit,
    onQrCodeDetected: (ScanData) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        ScanCode(
            onQrCodeDetected = { output: String ->

                val data = if (output.startsWith("BEGIN:VCARD")) {

                    val name = Regex("""^\s*N:(.+)""", RegexOption.MULTILINE)
                        .find(output)?.groupValues?.get(1)?.trim()

                    val phone = Regex("""^\s*TEL:(.+)""", RegexOption.MULTILINE)
                        .find(output)?.groupValues?.get(1)?.trim()

                    val email = Regex("""^\s*EMAIL:(.+)""", RegexOption.MULTILINE)
                        .find(output)?.groupValues?.get(1)?.trim()

                    ScanData(
                        type = "Contact",
                        content = "$name\nPhone: $phone\nEmail: $email"
                    )


                } else if (output.startsWith("WIFI")) {

                    val ssid = output.substringAfter("S:").substringBefore(";")
                    val password = output.substringAfter("P:").substringBefore(";")
                    val encryption = output.substringAfter("T:").substringBefore(";")

                    ScanData(
                        type = "Wi-Fi",
                        content = "SSID: $ssid\nPassword: $password\nEncryption Type: $encryption"
                    )
                } else if (output.startsWith("geo")) {

                    val latAndLon = output.substringAfter("geo:")
                    ScanData(
                        type = "Geolocation",
                        content = latAndLon
                    )

                } else if (output.startsWith("http")) {
                    val originalLink = output.removePrefix("http://")
                    ScanData(
                        type = "Link",
                        content = originalLink
                    )
                } else if (output.startsWith("tel")) {
                    val telephone = output.substringAfter("tel:")
                    ScanData(
                        type = "Phone Number",
                        content = telephone
                    )
                } else {
                    val notes = output.substringAfter(":")
                    ScanData(
                        type = "Text",
                        content = notes
                    )
                }
                onQrCodeDetected.invoke(data)
            },
            onExit = {
                onExit.invoke()
            }
        )
    }
}