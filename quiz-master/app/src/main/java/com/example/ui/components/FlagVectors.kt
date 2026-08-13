package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Clean Vector Flag Renderer for Supported Countries (NO Emoji Flags)
 */
@Composable
fun CountryFlagVector(
    countryCode: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    Box(
        modifier = modifier
            .size(width = size * 1.4f, height = size)
            .clip(RoundedCornerShape(size * 0.18f))
            .border(1.dp, Color(0xFFD8CEBE), RoundedCornerShape(size * 0.18f))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = this.size.width
            val h = this.size.height

            when (countryCode.uppercase()) {
                "BD" -> { // Bangladesh: Green field + Red circle
                    drawRect(color = Color(0xFF006A4E))
                    drawCircle(color = Color(0xFFF42A41), radius = h * 0.33f, center = Offset(w * 0.45f, h * 0.5f))
                }
                "US" -> { // United States: Red/white stripes + Blue canton
                    drawRect(color = Color(0xFFB22234))
                    val stripeH = h / 7f
                    for (i in 1..6 step 2) {
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(0f, i * stripeH),
                            size = Size(w, stripeH)
                        )
                    }
                    drawRect(color = Color(0xFF3C3B6E), size = Size(w * 0.45f, stripeH * 4f))
                }
                "IN" -> { // India: Saffron, White, Green + Navy Blue Wheel
                    drawRect(color = Color(0xFFFF9933), size = Size(w, h / 3f))
                    drawRect(color = Color.White, topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
                    drawRect(color = Color(0xFF138808), topLeft = Offset(0f, 2f * h / 3f), size = Size(w, h / 3f))
                    drawCircle(color = Color(0xFF000080), radius = h * 0.12f, center = Offset(w * 0.5f, h * 0.5f))
                }
                "SA" -> { // Saudi Arabia: Green + White Sword/Calligraphy emblem
                    drawRect(color = Color(0xFF006C35))
                    val p = Path().apply {
                        moveTo(w * 0.25f, h * 0.65f)
                        lineTo(w * 0.75f, h * 0.65f)
                        lineTo(w * 0.75f, h * 0.72f)
                        lineTo(w * 0.25f, h * 0.72f)
                        close()
                    }
                    drawPath(path = p, color = Color.White)
                }
                "ES" -> { // Spain: Red, Yellow, Red
                    drawRect(color = Color(0xFFAA152F), size = Size(w, h * 0.25f))
                    drawRect(color = Color(0xFFF1BF00), topLeft = Offset(0f, h * 0.25f), size = Size(w, h * 0.5f))
                    drawRect(color = Color(0xFFAA152F), topLeft = Offset(0f, h * 0.75f), size = Size(w, h * 0.25f))
                }
                "FR" -> { // France: Blue, White, Red
                    drawRect(color = Color(0xFF002395), size = Size(w / 3f, h))
                    drawRect(color = Color.White, topLeft = Offset(w / 3f, 0f), size = Size(w / 3f, h))
                    drawRect(color = Color(0xFFED2939), topLeft = Offset(2f * w / 3f, 0f), size = Size(w / 3f, h))
                }
                "DE" -> { // Germany: Black, Red, Gold
                    drawRect(color = Color.Black, size = Size(w, h / 3f))
                    drawRect(color = Color(0xFFDD0000), topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
                    drawRect(color = Color(0xFFFFCC00), topLeft = Offset(0f, 2f * h / 3f), size = Size(w, h / 3f))
                }
                "JP" -> { // Japan: White + Red Sun Circle
                    drawRect(color = Color.White)
                    drawCircle(color = Color(0xFFBC002D), radius = h * 0.3f, center = Offset(w * 0.5f, h * 0.5f))
                }
                "KR" -> { // Korea: White + Taegeuk Circle
                    drawRect(color = Color.White)
                    drawCircle(color = Color(0xFFCD2E3A), radius = h * 0.25f, center = Offset(w * 0.5f, h * 0.5f))
                }
                "CN" -> { // China: Red + Yellow Star
                    drawRect(color = Color(0xFFDE2910))
                    drawCircle(color = Color(0xFFFFDE00), radius = h * 0.18f, center = Offset(w * 0.25f, h * 0.35f))
                }
                "BR" -> { // Brazil: Green field + Yellow Diamond + Blue Globe
                    drawRect(color = Color(0xFF009C3B))
                    val diamond = Path().apply {
                        moveTo(w * 0.5f, h * 0.15f)
                        lineTo(w * 0.85f, h * 0.5f)
                        lineTo(w * 0.5f, h * 0.85f)
                        lineTo(w * 0.15f, h * 0.5f)
                        close()
                    }
                    drawPath(path = diamond, color = Color(0xFFFFDF00))
                    drawCircle(color = Color(0xFF002776), radius = h * 0.22f, center = Offset(w * 0.5f, h * 0.5f))
                }
                "RU" -> { // Russia: White, Blue, Red
                    drawRect(color = Color.White, size = Size(w, h / 3f))
                    drawRect(color = Color(0xFF0039A6), topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
                    drawRect(color = Color(0xDDAA0000), topLeft = Offset(0f, 2f * h / 3f), size = Size(w, h / 3f))
                }
                "IT" -> { // Italy: Green, White, Red
                    drawRect(color = Color(0xFF009246), size = Size(w / 3f, h))
                    drawRect(color = Color.White, topLeft = Offset(w / 3f, 0f), size = Size(w / 3f, h))
                    drawRect(color = Color(0xFFCE2B37), topLeft = Offset(2f * w / 3f, 0f), size = Size(w / 3f, h))
                }
                "TR" -> { // Turkey: Red + White Crescent/Star
                    drawRect(color = Color(0xFFE30A17))
                    drawCircle(color = Color.White, radius = h * 0.28f, center = Offset(w * 0.45f, h * 0.5f))
                    drawCircle(color = Color(0xFFE30A17), radius = h * 0.22f, center = Offset(w * 0.5f, h * 0.5f))
                }
                "PK" -> { // Pakistan: White Stripe + Dark Green Crescent
                    drawRect(color = Color.White, size = Size(w * 0.28f, h))
                    drawRect(color = Color(0xFF00401A), topLeft = Offset(w * 0.28f, 0f), size = Size(w * 0.72f, h))
                    drawCircle(color = Color.White, radius = h * 0.25f, center = Offset(w * 0.62f, h * 0.5f))
                    drawCircle(color = Color(0xFF00401A), radius = h * 0.20f, center = Offset(w * 0.66f, h * 0.48f))
                }
                else -> { // Generic Default Flag
                    drawRect(color = Color(0xFF4A3B32))
                    drawCircle(color = Color(0xFFC59B27), radius = h * 0.25f, center = Offset(w * 0.5f, h * 0.5f))
                }
            }
        }
    }
}
