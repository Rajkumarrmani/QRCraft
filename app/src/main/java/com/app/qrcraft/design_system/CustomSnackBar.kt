package com.app.qrcraft.design_system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.qrcraft.R
import com.app.qrcraft.ui.theme.Text
import com.app.qrcraft.ui.theme.success
import com.app.qrcraft.ui.theme.test

@Preview
@Composable
private fun CustomSnackBarPreview() {
    CustomSnackBar()
}

@Composable
fun CustomSnackBar(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.success,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(
            3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.check),
                contentDescription = "Success Icon",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            Text(
                text = "Camera permission granted",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}