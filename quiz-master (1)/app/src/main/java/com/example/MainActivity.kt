package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.AppLanguage
import com.example.data.LevelEngine
import com.example.data.QuizLevelData
import com.example.data.SupportedLanguages
import com.example.data.UserPreferencesRepository
import com.example.ui.components.LanguageModalBottomSheet
import com.example.ui.components.UnlockAnimationOverlay
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.LevelSelectionScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.TTSManager

sealed class AppScreen {
    object LanguageSelection : AppScreen()
    object LevelSelection : AppScreen()
    data class Quiz(val levelNumber: Int) : AppScreen()
}

class MainActivity : ComponentActivity() {

    private lateinit var prefsRepo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefsRepo = UserPreferencesRepository(applicationContext)

        // Initialize TTS with current language
        val initialLangCode = prefsRepo.getSelectedLanguage()
        val initialLangObj = SupportedLanguages.getByCode(initialLangCode)
        TTSManager.initialize(applicationContext, initialLangObj.ttsLocale)

        setContent {
            MyApplicationTheme {
                MainAppContent(prefsRepo = prefsRepo)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TTSManager.shutdown()
    }
}

@Composable
fun MainAppContent(prefsRepo: UserPreferencesRepository) {
    var isFirstLaunch by remember { mutableStateOf(prefsRepo.isFirstLaunch()) }
    var currentLanguageCode by remember { mutableStateOf(prefsRepo.getSelectedLanguage()) }

    var currentScreen by remember {
        mutableStateOf<AppScreen>(
            if (isFirstLaunch) AppScreen.LanguageSelection else AppScreen.LevelSelection
        )
    }

    var unlockedLevels by remember { mutableStateOf(prefsRepo.getUnlockedLevels()) }
    var completedLevels by remember { mutableStateOf(prefsRepo.getCompletedLevels()) }
    var userScore by remember { mutableIntStateOf(prefsRepo.getUserScore()) }
    var levelProgressMap by remember {
        mutableStateOf(
            unlockedLevels.associateWith { prefsRepo.getLevelProgress(it) }
        )
    }

    var showLanguageModal by remember { mutableStateOf(false) }
    var unlockedLevelAnimationTarget by remember { mutableStateOf<Int?>(null) }

    // Update TTS when language changes
    LaunchedEffect(currentLanguageCode) {
        val langObj = SupportedLanguages.getByCode(currentLanguageCode)
        TTSManager.updateLanguage(langObj.ttsLocale)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
            is AppScreen.LanguageSelection -> {
                LanguageSelectionScreen(
                    onLanguageConfirmed = { selectedLang ->
                        currentLanguageCode = selectedLang.code
                        prefsRepo.setSelectedLanguage(selectedLang.code)
                        prefsRepo.setSelectedCountry(selectedLang.countryCode)
                        prefsRepo.setFirstLaunchCompleted()
                        isFirstLaunch = false
                        currentScreen = AppScreen.LevelSelection
                    }
                )
            }

            is AppScreen.LevelSelection -> {
                LevelSelectionScreen(
                    currentLanguageCode = currentLanguageCode,
                    unlockedLevels = unlockedLevels,
                    completedLevels = completedLevels,
                    levelProgressMap = levelProgressMap,
                    userScore = userScore,
                    onLevelSelected = { levelNum ->
                        currentScreen = AppScreen.Quiz(levelNum)
                    },
                    onOpenLanguageSwitcher = {
                        showLanguageModal = true
                    }
                )
            }

            is AppScreen.Quiz -> {
                val levelData = remember(screen.levelNumber, currentLanguageCode) {
                    LevelEngine.generateLevel(screen.levelNumber, currentLanguageCode)
                }

                BackHandler {
                    TTSManager.stop()
                    currentScreen = AppScreen.LevelSelection
                }

                QuizScreen(
                    levelData = levelData,
                    currentLanguageCode = currentLanguageCode,
                    onQuizCompleted = { scoreEarned, _ ->
                        TTSManager.stop()
                        prefsRepo.addScore(scoreEarned)
                        prefsRepo.markLevelCompleted(screen.levelNumber, 100)

                        // Update State
                        unlockedLevels = prefsRepo.getUnlockedLevels()
                        completedLevels = prefsRepo.getCompletedLevels()
                        userScore = prefsRepo.getUserScore()
                        levelProgressMap = unlockedLevels.associateWith { prefsRepo.getLevelProgress(it) }

                        // Trigger key unlock animation for newly unlocked level
                        val nextLevel = screen.levelNumber + 1
                        unlockedLevelAnimationTarget = nextLevel

                        currentScreen = AppScreen.LevelSelection
                    },
                    onBackClick = {
                        TTSManager.stop()
                        currentScreen = AppScreen.LevelSelection
                    }
                )
            }
        }

        // Language Switcher Modal Bottom Sheet
        if (showLanguageModal) {
            LanguageModalBottomSheet(
                currentLanguageCode = currentLanguageCode,
                onLanguageSelected = { newLang ->
                    currentLanguageCode = newLang.code
                    prefsRepo.setSelectedLanguage(newLang.code)
                    prefsRepo.setSelectedCountry(newLang.countryCode)
                    showLanguageModal = false
                },
                onDismissRequest = {
                    showLanguageModal = false
                }
            )
        }

        // Unlock Key Animation Overlay
        unlockedLevelAnimationTarget?.let { newUnlockedLevel ->
            UnlockAnimationOverlay(
                unlockedLevelNumber = newUnlockedLevel,
                onAnimationEnd = {
                    unlockedLevelAnimationTarget = null
                }
            )
        }
    }
}
