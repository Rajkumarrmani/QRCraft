package com.app.qrcraft

import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.qrcraft.ui.theme.QRCraftTheme
import kotlinx.coroutines.delay

@Preview
@Composable
private fun SplashScreenPreview() {
    QRCraftTheme {
        SplashScreen(
            homePage = {}
        )
    }
}

@Composable
fun SplashScreen(homePage: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface)
    ) {
        Image(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "splash screen logo",
        )
    }

    LaunchedEffect(Unit) {
        delay(2000)
        homePage.invoke()
    }
}