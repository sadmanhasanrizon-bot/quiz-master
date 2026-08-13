package com.example.data

/**
 * QuestionProvider supplies dynamic, infinite questions for any level and language
 * without hardcoded level limits or caps.
 */
object QuestionProvider {

    /**
     * Generates a list of questions dynamically for the requested level and language.
     * Supports unlimited levels (1 to infinity).
     */
    fun getQuestionsForLevel(levelNumber: Int, languageCode: String): List<QuizQuestion> {
        return LevelEngine.generateLevel(levelNumber, languageCode).questions
    }

    /**
     * Generates complete QuizLevelData for the requested level and language.
     */
    fun getLevelData(levelNumber: Int, languageCode: String): QuizLevelData {
        return LevelEngine.generateLevel(levelNumber, languageCode)
    }
}
