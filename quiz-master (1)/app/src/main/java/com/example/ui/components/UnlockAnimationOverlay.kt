package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NaturalAccentGold
import com.example.ui.theme.NaturalKeyGold
import kotlinx.coroutines.delay

@Composable
fun UnlockAnimationOverlay(
    unlockedLevelNumber: Int,
    onAnimationEnd: () -> Unit
) {
    val keyProgress = remember { Animatable(0f) }
    val keyRotation = remember { Animatable(0f) }
    val lockOpenProgress = remember { Animatable(0f) }
    val lightBurstScale = remember { Animatable(0f) }
    val lightBurstAlpha = remember { Animatable(1f) }

    LaunchedEffect(unlockedLevelNumber) {
        // Step 1: Key appears & moves to lock
        keyProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        delay(100)

        // Step 2: Key enters lock & rotates
        keyRotation.animateTo(
            targetValue = 90f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        delay(100)

        // Step 3: Lock opens & retracts
        lockOpenProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )

        // Step 4: Light burst particles
        lightBurstScale.animateTo(
            targetValue = 2.5f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        lightBurstAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 400)
        )

        delay(200)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        // Light Burst Effect
        if (lightBurstScale.value > 0f) {
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .scale(lightBurstScale.value)
                    .alpha(lightBurstAlpha.value)
            ) {
                drawCircle(color = NaturalAccentGold, radius = size.width * 0.4f)
                drawCircle(color = Color.White, radius = size.width * 0.2f)
            }
        }

        // Lock & Key Container
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Lock
            val lockAlpha = 1f - lockOpenProgress.value
            val lockScale = 1f - (lockOpenProgress.value * 0.3f)
            if (lockAlpha > 0f) {
                PremiumLockIcon(
                    modifier = Modifier
                        .scale(lockScale)
                        .alpha(lockAlpha),
                    size = 80.dp
                )
            }

            // Key moving toward lock
            val keyYOffset = (1f - keyProgress.value) * 120f
            val keyAlpha = keyProgress.value * (1f - lockOpenProgress.value)

            if (keyAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .offset(y = keyYOffset.dp)
                        .alpha(keyAlpha)
                ) {
                    PremiumKeyIcon(
                        size = 64.dp,
                        angle = keyRotation.value
                    )
                }
            }
        }

        // Localized Title
        Text(
            text = "Level $unlockedLevelNumber Unlocked!",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = NaturalKeyGold,
                fontSize = 22.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
                .alpha(keyProgress.value)
        )
    }
}
