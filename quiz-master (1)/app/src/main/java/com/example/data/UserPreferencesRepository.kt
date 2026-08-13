package com.example.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("quiz_master_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FIRST_LAUNCH = "key_first_launch"
        private const val KEY_SELECTED_LANGUAGE = "key_selected_language"
        private const val KEY_SELECTED_COUNTRY = "key_selected_country"
        private const val KEY_UNLOCKED_LEVELS = "key_unlocked_levels"
        private const val KEY_COMPLETED_LEVELS = "key_completed_levels"
        private const val KEY_USER_SCORE = "key_user_score"
        private const val KEY_PROGRESS_PREFIX = "key_level_progress_"
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun getSelectedLanguage(): String {
        return prefs.getString(KEY_SELECTED_LANGUAGE, "bn") ?: "bn"
    }

    fun setSelectedLanguage(languageCode: String) {
        prefs.edit().putString(KEY_SELECTED_LANGUAGE, languageCode).apply()
    }

    fun getSelectedCountry(): String {
        return prefs.getString(KEY_SELECTED_COUNTRY, "BD") ?: "BD"
    }

    fun setSelectedCountry(countryCode: String) {
        prefs.edit().putString(KEY_SELECTED_COUNTRY, countryCode).apply()
    }

    fun getUnlockedLevels(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_UNLOCKED_LEVELS, setOf("1")) ?: setOf("1")
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet().ifEmpty { setOf(1) }
    }

    fun unlockLevel(levelNumber: Int) {
        val currentUnlocked = getUnlockedLevels().toMutableSet()
        currentUnlocked.add(levelNumber)
        val stringSet = currentUnlocked.map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_UNLOCKED_LEVELS, stringSet).apply()
    }

    fun getCompletedLevels(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_COMPLETED_LEVELS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun markLevelCompleted(levelNumber: Int, progressPercent: Int = 100) {
        val currentCompleted = getCompletedLevels().toMutableSet()
        currentCompleted.add(levelNumber)
        val stringSet = currentCompleted.map { it.toString() }.toSet()

        prefs.edit()
            .putStringSet(KEY_COMPLETED_LEVELS, stringSet)
            .putInt(KEY_PROGRESS_PREFIX + levelNumber, progressPercent)
            .apply()

        // Automatically unlock next level
        unlockLevel(levelNumber + 1)
    }

    fun getLevelProgress(levelNumber: Int): Int {
        if (getCompletedLevels().contains(levelNumber)) {
            return 100
        }
        return prefs.getInt(KEY_PROGRESS_PREFIX + levelNumber, 0)
    }

    fun setLevelProgress(levelNumber: Int, percent: Int) {
        prefs.edit().putInt(KEY_PROGRESS_PREFIX + levelNumber, percent).apply()
    }

    fun getUserScore(): Int {
        return prefs.getInt(KEY_USER_SCORE, 0)
    }

    fun addScore(points: Int) {
        val current = getUserScore()
        prefs.edit().putInt(KEY_USER_SCORE, current + points).apply()
    }
}
