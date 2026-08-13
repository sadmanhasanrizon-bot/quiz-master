package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LocalizationDictionary
import com.example.data.SupportedLanguages
import com.example.ui.components.CountryFlagVector
import com.example.ui.components.PremiumCheckIcon
import com.example.ui.components.PremiumLockIcon
import com.example.ui.components.PremiumPlayIcon
import com.example.ui.theme.NaturalAccentGold
import com.example.ui.theme.NaturalBackgroundLight
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalPrimaryLight
import com.example.ui.theme.NaturalSecondaryLight
import com.example.ui.theme.NaturalSurfaceLight
import com.example.ui.theme.NaturalTextPrimaryLight
import com.example.ui.theme.NaturalTextSecondaryLight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    currentLanguageCode: String,
    unlockedLevels: Set<Int>,
    completedLevels: Set<Int>,
    levelProgressMap: Map<Int, Int>,
    userScore: Int,
    onLevelSelected: (Int) -> Unit,
    onOpenLanguageSwitcher: () -> Unit
) {
    val strings = LocalizationDictionary.getStrings(currentLanguageCode)
    val currentLangObj = SupportedLanguages.getByCode(currentLanguageCode)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Determine highest level to display (Unlimited generator)
    val maxUnlocked = unlockedLevels.maxOrNull() ?: 1
    var visibleLevelCount by remember { mutableIntStateOf(maxOf(30, maxUnlocked + 15)) }

    // Ensure visibleLevelCount grows as user unlocks higher levels
    LaunchedEffect(maxUnlocked) {
        if (visibleLevelCount < maxUnlocked + 15) {
            visibleLevelCount = maxUnlocked + 15
        }
    }

    val listState = rememberLazyListState()

    // Infinite list scroll listener: appends 20 more levels as user scrolls near bottom
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= visibleLevelCount - 5) {
                    visibleLevelCount += 20
                }
            }
    }

    val levelList = remember(visibleLevelCount) {
        (1..visibleLevelCount).toList()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_logo_1786643462553),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.appTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = NaturalTextPrimaryLight
                            )
                        }

                        // Language Switcher Header Button
                        Surface(
                            onClick = onOpenLanguageSwitcher,
                            shape = RoundedCornerShape(20.dp),
                            color = NaturalSurfaceLight,
                            modifier = Modifier.border(1.dp, NaturalBorderLight, RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CountryFlagVector(
                                    countryCode = currentLangObj.countryCode,
                                    size = 20.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentLangObj.nativeName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextPrimaryLight
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NaturalBackgroundLight
                )
            )
        },
        containerColor = NaturalBackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Stats Header with Artistic Flair styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFFE0DDD7), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.scoreText.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF8A887C)
                    )
                    Text(
                        text = "$userScore",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD4A373)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = strings.totalProgress.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF8A887C)
                    )
                    Text(
                        text = "${completedLevels.size} ${strings.levelCompleted}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A5A40)
                    )
                }
            }

            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Text(
                    text = strings.levelSelectionTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2C2C24)
                )
                Text(
                    text = "MASTER YOUR KNOWLEDGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF8A887C)
                )
            }

            // Scrollable Levels List with STABLE KEYS and Infinite Scroll State
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = levelList,
                    key = { levelNum -> "stable_level_card_$levelNum" } // STABLE UNIQUE KEY
                ) { levelNum ->
                    val isUnlocked = unlockedLevels.contains(levelNum)
                    val isCompleted = completedLevels.contains(levelNum)
                    val progress = levelProgressMap[levelNum] ?: if (isCompleted) 100 else 0
                    val isCurrent = levelNum == maxUnlocked && !isCompleted

                    Level3DCard(
                        levelNumber = levelNum,
                        title = "${strings.levelTitle} $levelNum",
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        progress = progress,
                        isCurrent = isCurrent,
                        strings = strings,
                        onLevelClick = {
                            if (isUnlocked) {
                                onLevelSelected(levelNum)
                            } else {
                                coroutineScope.launch {
                                    val msg = String.format(strings.levelLockedMsg, levelNum, levelNum - 1)
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Level3DCard(
    levelNumber: Int,
    title: String,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    progress: Int,
    isCurrent: Boolean,
    strings: com.example.data.LocalizedStrings,
    onLevelClick: () -> Unit
) {
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val cardBg = when {
        isUnlocked -> Color.White
        else -> Color(0xFFE6E1D6) // Artistic locked canvas background
    }

    val cardBorderColor = when {
        isCompleted -> Color(0xFF5A5A40) // Olive / Moss
        isCurrent -> Color(0xFFD4A373) // Ochre Gold
        isUnlocked -> Color(0xFFD4A373)
        else -> Color(0xFFB8B4A8)
    }

    val badgeBg = when {
        isCompleted -> Color(0xFF5A5A40)
        isCurrent -> Color(0xFFD4A373)
        isUnlocked -> Color(0xFF5A5A40)
        else -> Color(0xFFB8B4A8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 2.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                if (!isUnlocked) {
                    coroutineScope.launch {
                        shakeOffset.animateTo(12f, tween(50))
                        shakeOffset.animateTo(-12f, tween(50))
                        shakeOffset.animateTo(6f, tween(50))
                        shakeOffset.animateTo(0f, tween(50))
                    }
                }
                onLevelClick()
            }
            .testTag("level_card_$levelNumber"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Level Number Badge
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (levelNumber < 10) "0$levelNumber" else "$levelNumber",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isUnlocked) Color(0xFF2C2C24) else Color(0xFF8A887C)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val statusText = when {
                        isCompleted -> strings.levelCompleted
                        isUnlocked -> "${strings.progress} $progress%"
                        else -> strings.levelLocked
                    }

                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) Color(0xFF5A5A40) else Color(0xFF8A887C)
                    )

                    if (isUnlocked) {
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier
                                .width(110.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isCompleted) Color(0xFF5A5A40) else Color(0xFFD4A373),
                            trackColor = Color(0xFFF0EEE9)
                        )
                    }
                }
            }

            // Status Icon (Lock / Play / Checkmark)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFF5A5A40)
                            isCurrent -> Color(0xFFD4A373)
                            isUnlocked -> Color(0xFF5A5A40)
                            else -> Color(0xFFB8B4A8)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> PremiumCheckIcon(size = 24.dp, tint = Color.White)
                    isUnlocked -> PremiumPlayIcon(size = 22.dp, tint = Color.White)
                    else -> PremiumLockIcon(size = 24.dp)
                }
            }
        }
    }
}
