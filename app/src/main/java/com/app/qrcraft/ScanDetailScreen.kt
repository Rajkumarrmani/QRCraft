package com.app.qrcraft

import android.R.attr.name
import android.R.attr.navigationIcon
import android.R.attr.text
import android.R.attr.type
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.app.qrcraft.design_system.CustomButton
import com.app.qrcraft.helper.toAnnotatedLinkString
import com.app.qrcraft.navigation.ScanData
import com.app.qrcraft.ui.theme.QRCraftTheme
import com.google.android.material.color.MaterialColors

//@Preview(showBackground = true)
@Preview(showBackground = true, device = "spec:width=640dp,height=360dp,dpi=480")
@Composable
private fun ScanDetailScreenPreview() {

    val data = ScanData(
        type = "Text",
        content = "To make a Column scrollable the verticalScroll() function of Modifier can be called upon it. This is the simplest way to allow scrolling on a Column when the content size exceeds the maximum size of the Column The first parameter of the verticalScroll() function is state, which is a required parameter. It is used to maintain the state of the scroll, for example identifying the scroll position of the currently displayed list."
    )

    QRCraftTheme {
        ScanDetailScreen(
            scanData = data,
            onBack = { }
        )
    }

}

@SuppressLint("UseKtx")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailScreen(
    scanData: ScanData,
    minimizedMaxLines: Int = 6,
    onBack: () -> Unit,
) {

    val annotatedText = remember(scanData.content) {
        scanData.content.toAnnotatedLinkString()
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isTextOverflow by rememberSaveable { mutableStateOf(false) }
    var isShare by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 50.dp),
                        text = "Scan Result",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack::invoke }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.onSurface)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 60.dp, start = 10.dp, end = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(top = 80.dp)
                    ) {

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            text = scanData.type,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                                .clickable {
                                    if(scanData.type == "Link") {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scanData.content))
                                        context.startActivity(intent)
                                    }
                                },
                            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = { textLayoutResult ->
                                if (!expanded) {
                                    isTextOverflow = textLayoutResult.hasVisualOverflow
                                }
                            },
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = if (scanData.type == "Contact") TextAlign.Center
                            else TextAlign.Start,
                        )

                        if (isTextOverflow || expanded) {
                            Text(
                                text = if (expanded) "Show less" else "Show more",
                                color = MaterialTheme.colorScheme.surfaceDim,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clickable { expanded = !expanded },
                                textAlign = TextAlign.Left,
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            CustomButton(
                                modifier = Modifier.weight(1f),
                                onClick = { isShare = true },
                                icon = ImageVector.vectorResource(id = R.drawable.shareicon),
                                text = "Share"
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            CustomButton(
                                modifier = Modifier.weight(1f),
                                onClick = { copyToClipboard(context, scanData.content) },
                                icon = ImageVector.vectorResource(id = R.drawable.copyicon),
                                text = "Copy"
                            )
                        }

                    }


                }

                Image(
                    modifier = Modifier
                        .wrapContentSize()
                        .size(150.dp)
                        .align(Alignment.TopCenter),
                    painter = painterResource(id = R.drawable.sampleqr),
                    contentDescription = "QR Code"
                )


            }

            if (isShare) {
                isShare = false
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, scanData.content)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(context, shareIntent, null)
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
}


