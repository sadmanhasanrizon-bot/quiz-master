package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocalizationDictionary
import com.example.data.QuizLevelData
import com.example.data.SupportedLanguages
import com.example.ui.components.PremiumBackIcon
import com.example.ui.components.PremiumCheckIcon
import com.example.ui.components.PremiumSoundIcon
import com.example.ui.components.PremiumStarIcon
import com.example.ui.theme.NaturalAccentGold
import com.example.ui.theme.NaturalBackgroundLight
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalError
import com.example.ui.theme.NaturalPrimaryLight
import com.example.ui.theme.NaturalSecondaryLight
import com.example.ui.theme.NaturalSurfaceLight
import com.example.ui.theme.NaturalTextPrimaryLight
import com.example.ui.theme.NaturalTextSecondaryLight
import com.example.utils.TTSManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    levelData: QuizLevelData,
    currentLanguageCode: String,
    onQuizCompleted: (scoreEarned: Int, totalQuestions: Int) -> Unit,
    onBackClick: () -> Unit
) {
    val strings = LocalizationDictionary.getStrings(currentLanguageCode)
    val langObj = remember(currentLanguageCode) { SupportedLanguages.getByCode(currentLanguageCode) }
    val speakingText by TTSManager.speakingText.collectAsState()

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var scoreEarned by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(levelData.timeLimitSeconds) }
    var isTimerActive by remember { mutableStateOf(true) }
    var showScoreboard by remember { mutableStateOf(false) }

    val currentQuestion = levelData.questions.getOrNull(currentQuestionIndex)
    val options = remember(currentQuestionIndex) {
        currentQuestion?.getShuffledOptions() ?: emptyList()
    }

    // Stop speech when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            TTSManager.stop()
        }
    }

    // AUTO-SPEAK QUESTION WHEN LEVEL OPENS OR QUESTION CHANGES
    LaunchedEffect(currentQuestionIndex, showScoreboard) {
        if (!showScoreboard && currentQuestion != null) {
            delay(300) // gentle delay for smooth screen entrance transition
            TTSManager.speak(currentQuestion.questionText, langObj.ttsLocale)
        } else if (showScoreboard) {
            delay(300)
            val congratsMsg = "${strings.levelSuccessTitle} ${strings.levelSuccessSub} ${strings.scoreText}: $scoreEarned"
            TTSManager.speak(congratsMsg, langObj.ttsLocale)
        }
    }

    // Timer countdown
    LaunchedEffect(currentQuestionIndex, isTimerActive, isAnswerSubmitted, showScoreboard) {
        if (isTimerActive && timeLeft > 0 && !isAnswerSubmitted && !showScoreboard) {
            while (timeLeft > 0 && !isAnswerSubmitted) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0 && !isAnswerSubmitted) {
                // Time expired -> auto submit empty
                isAnswerSubmitted = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${strings.levelTitle} ${levelData.levelNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2C2C24)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            TTSManager.stop()
                            onBackClick()
                        },
                        modifier = Modifier.testTag("quiz_back_button")
                    ) {
                        PremiumBackIcon(size = 24.dp)
                    }
                },
                actions = {
                    // Timer Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NaturalSurfaceLight,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .border(1.5.dp, Color(0xFFE0DDD7), RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "${timeLeft}s",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = if (timeLeft <= 10) Color(0xFFD45D50) else Color(0xFFD4A373),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NaturalBackgroundLight)
            )
        },
        containerColor = NaturalBackgroundLight
    ) { innerPadding ->
        if (showScoreboard) {
            // SCOREBOARD / LEVEL COMPLETION SUMMARY VIEW
            LevelScoreboardView(
                levelNumber = levelData.levelNumber,
                scoreEarned = scoreEarned,
                correctCount = correctCount,
                totalQuestions = levelData.questions.size,
                strings = strings,
                onContinue = {
                    TTSManager.stop()
                    onQuizCompleted(scoreEarned, levelData.questions.size)
                },
                onRetry = {
                    TTSManager.stop()
                    showScoreboard = false
                    currentQuestionIndex = 0
                    selectedOption = null
                    isAnswerSubmitted = false
                    scoreEarned = 0
                    correctCount = 0
                    timeLeft = levelData.timeLimitSeconds
                },
                onBackToLevels = {
                    TTSManager.stop()
                    onQuizCompleted(scoreEarned, levelData.questions.size)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            if (currentQuestion == null) return@Scaffold

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Question Progress Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.questionTitle} ${currentQuestionIndex + 1}/${levelData.questions.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF8A887C)
                    )
                    Text(
                        text = "${strings.scoreText}: $scoreEarned",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color(0xFFD4A373)
                    )
                }

                val animatedProgress by animateFloatAsState(
                    targetValue = (currentQuestionIndex + 1) / levelData.questions.size.toFloat(),
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "progress"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFD4A373),
                    trackColor = Color(0xFFE0DDD7)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ANIMATED QUESTION CARD WITH SLIDE & FADE
                AnimatedContent(
                    targetState = currentQuestionIndex,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "question_transition"
                ) { _ ->
                    Column {
                        // QUESTION CARD WITH QUESTION TTS SOUND BUTTON
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color(0xFF5A5A40), RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = currentQuestion.questionText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF2C2C24)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    // QUESTION SOUND BUTTON (Speaks COMPLETE Question)
                                    val isQuestionSpeaking = speakingText == currentQuestion.questionText
                                    IconButton(
                                        onClick = {
                                            TTSManager.speak(currentQuestion.questionText, langObj.ttsLocale)
                                        },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .testTag("question_sound_button")
                                    ) {
                                        PremiumSoundIcon(
                                            size = 28.dp,
                                            isSpeaking = isQuestionSpeaking,
                                            tint = if (isQuestionSpeaking) Color(0xFFD4A373) else Color(0xFF2C2C24)
                                        )
                                    }
                                }

                                // HINT SECTION (If available)
                                currentQuestion.hint?.let { hint ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF2E6D0))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "${strings.hintTitle}: $hint",
                                            fontSize = 13.sp,
                                            color = Color(0xFF2C2C24),
                                            modifier = Modifier.weight(1f)
                                        )

                                        // HINT SOUND BUTTON
                                        val isHintSpeaking = speakingText == hint
                                        IconButton(
                                            onClick = {
                                                TTSManager.speak(hint, langObj.ttsLocale)
                                            },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .testTag("hint_sound_button")
                                        ) {
                                            PremiumSoundIcon(
                                                size = 20.dp,
                                                isSpeaking = isHintSpeaking,
                                                tint = if (isHintSpeaking) Color(0xFFD4A373) else Color(0xFF2C2C24)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 4 ANSWER OPTIONS (Each with separate sound button)
                        options.forEachIndexed { optIndex, optionText ->
                            val isSelected = selectedOption == optionText
                            val isCorrect = optionText == currentQuestion.correctAnswer

                            val optionBg = when {
                                isAnswerSubmitted && isCorrect -> Color(0xFFE2EFE4) // Soft artistic green
                                isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFF9E4E1) // Soft artistic red
                                isSelected -> Color(0xFFF0EEE9)
                                else -> Color.White
                            }

                            val optionBorder = when {
                                isAnswerSubmitted && isCorrect -> Color(0xFF5A5A40)
                                isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFD45D50)
                                isSelected -> Color(0xFFD4A373)
                                else -> Color(0xFFE0DDD7)
                            }

                            val animatedScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.02f else 1.0f,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "scale"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .scale(animatedScale)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(2.dp, optionBorder, RoundedCornerShape(20.dp))
                                    .clickable(enabled = !isAnswerSubmitted) {
                                        selectedOption = optionText
                                    }
                                    .testTag("option_card_$optIndex"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = optionBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Option Badge (1, 2, 3, 4)
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFFD4A373) else Color(0xFF5A5A40)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${optIndex + 1}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Text(
                                            text = optionText,
                                            fontSize = 16.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = Color(0xFF2C2C24)
                                        )
                                    }

                                    // SOUND BUTTON FOR OPTION
                                    val isOptionSpeaking = speakingText == optionText
                                    IconButton(
                                        onClick = {
                                            TTSManager.speak(optionText, langObj.ttsLocale)
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("option_sound_button_$optIndex")
                                    ) {
                                        PremiumSoundIcon(
                                            size = 22.dp,
                                            isSpeaking = isOptionSpeaking,
                                            tint = if (isOptionSpeaking) Color(0xFFD4A373) else Color(0xFF8A887C)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // EXPLANATION SECTION
                AnimatedVisibility(visible = isAnswerSubmitted) {
                    Column {
                        currentQuestion.explanation?.let { exp ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .border(1.5.dp, Color(0xFFD4A373), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF2E6))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = strings.explanationTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFFD4A373)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = exp,
                                            fontSize = 14.sp,
                                            color = Color(0xFF2C2C24)
                                        )
                                    }

                                    // EXPLANATION SOUND BUTTON
                                    val isExpSpeaking = speakingText == exp
                                    IconButton(
                                        onClick = {
                                            TTSManager.speak(exp, langObj.ttsLocale)
                                        },
                                        modifier = Modifier.testTag("explanation_sound_button")
                                    ) {
                                        PremiumSoundIcon(
                                            size = 24.dp,
                                            isSpeaking = isExpSpeaking,
                                            tint = if (isExpSpeaking) Color(0xFFD4A373) else Color(0xFF2C2C24)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Next Question or Complete Level Button
                        Button(
                            onClick = {
                                TTSManager.stop()
                                if (currentQuestionIndex < levelData.questions.size - 1) {
                                    currentQuestionIndex++
                                    selectedOption = null
                                    isAnswerSubmitted = false
                                    timeLeft = levelData.timeLimitSeconds
                                } else {
                                    // Open Scoreboard
                                    showScoreboard = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("next_question_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A5A40))
                        ) {
                            Text(
                                text = if (currentQuestionIndex < levelData.questions.size - 1)
                                    strings.nextQuestion else strings.completeLevel,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Submit Answer Button
                if (!isAnswerSubmitted) {
                    Button(
                        onClick = {
                            isAnswerSubmitted = true
                            if (selectedOption == currentQuestion.correctAnswer) {
                                scoreEarned += 20
                                correctCount++
                            }
                        },
                        enabled = selectedOption != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .padding(top = 12.dp)
                            .testTag("submit_answer_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD4A373),
                            disabledContainerColor = Color(0xFFD0C6B8)
                        )
                    ) {
                        Text(
                            text = strings.continueButton,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Modern Animated Scoreboard View shown upon level completion
 */
@Composable
fun LevelScoreboardView(
    levelNumber: Int,
    scoreEarned: Int,
    correctCount: Int,
    totalQuestions: Int,
    strings: com.example.data.LocalizedStrings,
    onContinue: () -> Unit,
    onRetry: () -> Unit,
    onBackToLevels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accuracyPercent = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
    val starCount = when {
        accuracyPercent >= 80 -> 3
        accuracyPercent >= 50 -> 2
        accuracyPercent > 0 -> 1
        else -> 0
    }

    Column(
        modifier = modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Celebratory Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF5A5A40), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${strings.levelTitle} $levelNumber ${strings.levelCompleted}",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color(0xFF5A5A40)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars Display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PremiumStarIcon(size = 44.dp, isFilled = starCount >= 1)
                    PremiumStarIcon(size = 56.dp, isFilled = starCount >= 2)
                    PremiumStarIcon(size = 44.dp, isFilled = starCount >= 3)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = strings.levelSuccessTitle,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = Color(0xFF2C2C24)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.levelSuccessSub,
                    fontSize = 14.sp,
                    color = Color(0xFF8A887C),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Summary Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = strings.scoreText.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A887C)
                        )
                        Text(
                            text = "+$scoreEarned",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD4A373)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFE0DDD7))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ACCURACY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A887C)
                        )
                        Text(
                            text = "$accuracyPercent%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF5A5A40)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0xFFE0DDD7))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = strings.questionTitle.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A887C)
                        )
                        Text(
                            text = "$correctCount/$totalQuestions",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2C2C24)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("scoreboard_continue_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A5A40))
        ) {
            Text(
                text = strings.continueButton,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Retry Button
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("scoreboard_retry_button"),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD4A373))
        ) {
            Text(
                text = strings.retryLevel,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFD4A373)
            )
        }
    }
}
