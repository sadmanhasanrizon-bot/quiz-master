package com.example.data

import android.content.Context

/**
 * QuizRepository acts as the central data management layer for levels, progress, and user scoring.
 * Interacts with QuestionProvider for dynamic infinite level generation.
 */
class QuizRepository(context: Context) {

    private val userPrefs = UserPreferencesRepository(context)

    /**
     * Retrieves level data dynamically for any level number (1 to infinity) in any supported language.
     */
    fun getLevel(levelNumber: Int, languageCode: String): QuizLevelData {
        return QuestionProvider.getLevelData(levelNumber, languageCode)
    }

    /**
     * Checks if a level is unlocked for the user.
     */
    fun isLevelUnlocked(levelNumber: Int): Boolean {
        return userPrefs.getUnlockedLevels().contains(levelNumber)
    }

    /**
     * Checks if a level has been completed by the user.
     */
    fun isLevelCompleted(levelNumber: Int): Boolean {
        return userPrefs.getCompletedLevels().contains(levelNumber)
    }

    /**
     * Gets the set of all unlocked level numbers.
     */
    fun getUnlockedLevels(): Set<Int> {
        return userPrefs.getUnlockedLevels()
    }

    /**
     * Gets the set of all completed level numbers.
     */
    fun getCompletedLevels(): Set<Int> {
        return userPrefs.getCompletedLevels()
    }

    /**
     * Marks a level as completed, saves score, and automatically unlocks the next sequential level.
     */
    fun completeLevel(levelNumber: Int, scoreEarned: Int) {
        userPrefs.addScore(scoreEarned)
        userPrefs.markLevelCompleted(levelNumber, 100)
    }

    /**
     * Gets total score accumulated across all levels.
     */
    fun getUserScore(): Int {
        return userPrefs.getUserScore()
    }
}
