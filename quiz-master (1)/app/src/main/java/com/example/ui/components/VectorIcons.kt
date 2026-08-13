package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NaturalAccentGold
import com.example.ui.theme.NaturalKeyGold
import com.example.ui.theme.NaturalLockMetallic
import com.example.ui.theme.NaturalSecondaryLight
import com.example.ui.theme.NaturalTextPrimaryLight

/**
 * Premium 3D Vector Padlock (No Emoji, No Chain)
 */
@Composable
fun PremiumLockIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = NaturalLockMetallic
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Shackle (Metallic Curved Arc)
        val shacklePath = Path().apply {
            moveTo(w * 0.3f, h * 0.45f)
            lineTo(w * 0.3f, h * 0.28f)
            cubicTo(w * 0.3f, h * 0.1f, w * 0.7f, h * 0.1f, w * 0.7f, h * 0.28f)
            lineTo(w * 0.7f, h * 0.45f)
        }

        drawPath(
            path = shacklePath,
            color = Color(0xFF5A5248),
            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
        )
        drawPath(
            path = shacklePath,
            color = Color(0xFFD4C8B8),
            style = Stroke(width = w * 0.06f, cap = StrokeCap.Round)
        )

        // Lock Body (3D Beveled Rounded Rect)
        val bodyRect = Size(w * 0.75f, h * 0.52f)
        val bodyTopLeft = Offset(w * 0.125f, h * 0.42f)

        // Base Shadow
        drawRoundRect(
            color = Color(0x66000000),
            topLeft = bodyTopLeft + Offset(0f, h * 0.04f),
            size = bodyRect,
            cornerRadius = CornerRadius(w * 0.12f)
        )

        // Metallic Body Gradient
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    tint,
                    Color(0xFF5C5245),
                    Color(0xFF38322B)
                )
            ),
            topLeft = bodyTopLeft,
            size = bodyRect,
            cornerRadius = CornerRadius(w * 0.12f)
        )

        // Highlight Bevel Edge
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x88FFFFFF), Color(0x00FFFFFF))
            ),
            topLeft = bodyTopLeft,
            size = Size(bodyRect.width, bodyRect.height * 0.3f),
            cornerRadius = CornerRadius(w * 0.12f)
        )

        // Keyhole (Center)
        val center = Offset(w * 0.5f, h * 0.65f)
        drawCircle(color = Color(0xFF1E1B18), radius = w * 0.08f, center = center)
        val keyholeSlot = Path().apply {
            moveTo(w * 0.46f, h * 0.65f)
            lineTo(w * 0.54f, h * 0.65f)
            lineTo(w * 0.56f, h * 0.78f)
            lineTo(w * 0.44f, h * 0.78f)
            close()
        }
        drawPath(path = keyholeSlot, color = Color(0xFF1E1B18))
    }
}

/**
 * Premium 3D Golden Key
 */
@Composable
fun PremiumKeyIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    angle: Float = 0f
) {
    Canvas(modifier = modifier.size(size)) {
        rotate(degrees = angle) {
            val w = this.size.width
            val h = this.size.height

            // Key Ring Head
            val ringCenter = Offset(w * 0.3f, h * 0.5f)
            val ringRadius = w * 0.22f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NaturalKeyGold, Color(0xFF8C6D1B)),
                    center = ringCenter,
                    radius = ringRadius
                ),
                radius = ringRadius,
                center = ringCenter
            )
            drawCircle(color = Color(0xFFFAF6F0), radius = ringRadius * 0.45f, center = ringCenter)

            // Shaft
            val shaftPath = Path().apply {
                moveTo(w * 0.48f, h * 0.44f)
                lineTo(w * 0.85f, h * 0.44f)
                lineTo(w * 0.85f, h * 0.56f)
                lineTo(w * 0.48f, h * 0.56f)
                close()
            }
            drawPath(
                path = shaftPath,
                brush = Brush.verticalGradient(
                    colors = listOf(NaturalKeyGold, Color(0xFF8C6D1B))
                )
            )

            // Teeth
            val teethPath = Path().apply {
                moveTo(w * 0.72f, h * 0.56f)
                lineTo(w * 0.72f, h * 0.72f)
                lineTo(w * 0.78f, h * 0.72f)
                lineTo(w * 0.78f, h * 0.56f)
                moveTo(w * 0.81f, h * 0.56f)
                lineTo(w * 0.81f, h * 0.68f)
                lineTo(w * 0.86f, h * 0.68f)
                lineTo(w * 0.86f, h * 0.56f)
            }
            drawPath(path = teethPath, color = NaturalKeyGold)
        }
    }
}

/**
 * Vector Sound / Speaker Icon with Voice Waves Animation
 */
@Composable
fun PremiumSoundIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    isSpeaking: Boolean = false,
    tint: Color = NaturalTextPrimaryLight
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_waves")
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Speaker Body
        val speakerPath = Path().apply {
            moveTo(w * 0.15f, h * 0.38f)
            lineTo(w * 0.32f, h * 0.38f)
            lineTo(w * 0.52f, h * 0.22f)
            lineTo(w * 0.52f, h * 0.78f)
            lineTo(w * 0.32f, h * 0.62f)
            lineTo(w * 0.15f, h * 0.62f)
            close()
        }
        drawPath(path = speakerPath, color = tint)

        // Wave 1
        val waveColor = if (isSpeaking) NaturalAccentGold.copy(alpha = waveAlpha) else tint.copy(alpha = 0.7f)
        val wave1 = Path().apply {
            moveTo(w * 0.64f, h * 0.35f)
            cubicTo(w * 0.72f, h * 0.42f, w * 0.72f, h * 0.58f, w * 0.64f, h * 0.65f)
        }
        drawPath(path = wave1, color = waveColor, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))

        // Wave 2
        val wave2 = Path().apply {
            moveTo(w * 0.76f, h * 0.25f)
            cubicTo(w * 0.88f, h * 0.38f, w * 0.88f, h * 0.62f, w * 0.76f, h * 0.75f)
        }
        drawPath(
            path = wave2,
            color = if (isSpeaking) NaturalAccentGold.copy(alpha = 1.0f - waveAlpha + 0.2f) else tint.copy(alpha = 0.4f),
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Premium Vector Checkmark Icon
 */
@Composable
fun PremiumCheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    tint: Color = NaturalSecondaryLight
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val checkPath = Path().apply {
            moveTo(w * 0.2f, h * 0.5f)
            lineTo(w * 0.42f, h * 0.72f)
            lineTo(w * 0.82f, h * 0.28f)
        }
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(width = w * 0.15f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Vector Play Triangle Icon
 */
@Composable
fun PremiumPlayIcon(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    tint: Color = NaturalSecondaryLight
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val playPath = Path().apply {
            moveTo(w * 0.3f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.5f)
            lineTo(w * 0.3f, h * 0.78f)
            close()
        }
        drawPath(path = playPath, color = tint)
    }
}

/**
 * Back Arrow Vector
 */
@Composable
fun PremiumBackIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = NaturalTextPrimaryLight
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val arrowPath = Path().apply {
            moveTo(w * 0.68f, h * 0.2f)
            lineTo(w * 0.32f, h * 0.5f)
            lineTo(w * 0.68f, h * 0.8f)
        }
        drawPath(
            path = arrowPath,
            color = tint,
            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Premium Vector Star Icon for Level Completion Scoreboard
 */
@Composable
fun PremiumStarIcon(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    isFilled: Boolean = true,
    fillColor: Color = NaturalAccentGold,
    emptyColor: Color = Color(0xFFD6D1C7)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val starPath = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            lineTo(w * 0.63f, h * 0.35f)
            lineTo(w * 0.95f, h * 0.38f)
            lineTo(w * 0.71f, h * 0.60f)
            lineTo(w * 0.78f, h * 0.92f)
            lineTo(w * 0.5f, h * 0.75f)
            lineTo(w * 0.22f, h * 0.92f)
            lineTo(w * 0.29f, h * 0.60f)
            lineTo(w * 0.05f, h * 0.38f)
            lineTo(w * 0.37f, h * 0.35f)
            close()
        }

        if (isFilled) {
            drawPath(path = starPath, color = fillColor)
        } else {
            drawPath(path = starPath, color = emptyColor)
        }
    }
}
