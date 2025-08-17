package com.app.qrcraft.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable
    data object SplashScreen : Destination
    @Serializable
    data object Scan : Destination
    @Serializable
    data class ScanDetail(val scanData: ScanData) : Destination
}

@Serializable
data class ScanData(
    val type: String,
    val content: String
)