package com.app.qrcraft.helper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun String.toAnnotatedLinkString(
): AnnotatedString {
    return buildAnnotatedString {
        val regex = "(https?://\\S+)".toRegex()
        var lastIndex = 0

        regex.findAll(this@toAnnotatedLinkString).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1

            append(this@toAnnotatedLinkString.substring(lastIndex, start))

            pushStringAnnotation(tag = "URL", annotation = match.value)
            withStyle(
                style = SpanStyle(
                    background = Color.Yellow.copy(alpha = 0.1f),
                )
            ) {
                append(match.value)
            }
            pop()

            lastIndex = end
        }

        if (lastIndex < this@toAnnotatedLinkString.length) {
            append(this@toAnnotatedLinkString.substring(lastIndex))
        }
    }
}
