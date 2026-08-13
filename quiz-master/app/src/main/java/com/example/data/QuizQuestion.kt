package com.example.data

enum class QuestionType {
    FACT_BASED,
    SYNONYM_MEANING,
    RIDDLE,
    ODD_ONE_OUT,
    CAUSE_EFFECT,
    FILL_IN_BLANK,
    MATH_LOGIC,
    COUNTRY_TRIVIA
}

data class QuizQuestion(
    val id: String,
    val language: String,
    val type: QuestionType,
    val questionText: String,
    val correctAnswer: String,
    val wrongAnswers: List<String>,
    val difficulty: Int,
    val hint: String? = null,
    val explanation: String? = null
) {
    // Shuffled 4 options
    fun getShuffledOptions(): List<String> {
        val allOptions = ArrayList<String>()
        allOptions.add(correctAnswer)
        allOptions.addAll(wrongAnswers)
        // Fisher-Yates Shuffle
        val n = allOptions.size
        for (i in n - 1 downTo 1) {
            val j = (0..i).random()
            val temp = allOptions[i]
            allOptions[i] = allOptions[j]
            allOptions[j] = temp
        }
        return allOptions
    }
}

data class QuizLevelData(
    val levelNumber: Int,
    val title: String,
    val questions: List<QuizQuestion>,
    val timeLimitSeconds: Int,
    val requiredPreviousLevel: Int?
)
