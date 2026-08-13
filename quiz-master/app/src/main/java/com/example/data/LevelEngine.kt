package com.example.data

import kotlin.math.abs
import kotlin.math.max

object LevelEngine {

    /**
     * Dynamically generates level data for ANY level number (1 to infinity) in ANY language.
     */
    fun generateLevel(levelNumber: Int, languageCode: String): QuizLevelData {
        val strings = LocalizationDictionary.getStrings(languageCode)
        val title = "${strings.levelTitle} $levelNumber"
        val difficulty = calculateDifficulty(levelNumber)
        val timeLimit = calculateTimeLimit(levelNumber)
        val questions = generateQuestionsForLevel(levelNumber, languageCode, difficulty)

        return QuizLevelData(
            levelNumber = levelNumber,
            title = title,
            questions = questions,
            timeLimitSeconds = timeLimit,
            requiredPreviousLevel = if (levelNumber > 1) levelNumber - 1 else null
        )
    }

    private fun calculateDifficulty(levelNumber: Int): Int {
        return when {
            levelNumber <= 10 -> 1
            levelNumber <= 25 -> 2
            levelNumber <= 50 -> 3
            levelNumber <= 100 -> 4
            else -> minOf(10, 5 + (levelNumber - 100) / 20)
        }
    }

    private fun calculateTimeLimit(levelNumber: Int): Int {
        return when {
            levelNumber <= 10 -> 60
            levelNumber <= 25 -> 50
            levelNumber <= 50 -> 45
            else -> 30
        }
    }

    private fun generateQuestionsForLevel(
        levelNumber: Int,
        lang: String,
        difficulty: Int
    ): List<QuizQuestion> {
        val questionList = mutableListOf<QuizQuestion>()
        val count = 5 // 5 questions per level

        for (qIndex in 1..count) {
            val qType = determineQuestionType(levelNumber, qIndex)
            val question = buildDynamicQuestion(levelNumber, qIndex, lang, qType, difficulty)
            questionList.add(question)
        }

        return questionList
    }

    private fun determineQuestionType(levelNumber: Int, index: Int): QuestionType {
        val key = (levelNumber * 7 + index) % 8
        return when (key) {
            0 -> QuestionType.COUNTRY_TRIVIA
            1 -> QuestionType.MATH_LOGIC
            2 -> QuestionType.RIDDLE
            3 -> QuestionType.SYNONYM_MEANING
            4 -> QuestionType.ODD_ONE_OUT
            5 -> QuestionType.FILL_IN_BLANK
            6 -> QuestionType.CAUSE_EFFECT
            else -> QuestionType.FACT_BASED
        }
    }

    private fun buildDynamicQuestion(
        levelNumber: Int,
        qIndex: Int,
        lang: String,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        val id = "${lang}_L${levelNumber}_Q${qIndex}"

        return when (lang.lowercase()) {
            "bn" -> buildBengaliQuestion(id, levelNumber, qIndex, type, difficulty)
            "hi" -> buildHindiQuestion(id, levelNumber, qIndex, type, difficulty)
            "ar" -> buildArabicQuestion(id, levelNumber, qIndex, type, difficulty)
            "es" -> buildSpanishQuestion(id, levelNumber, qIndex, type, difficulty)
            "fr" -> buildFrenchQuestion(id, levelNumber, qIndex, type, difficulty)
            "de" -> buildGermanQuestion(id, levelNumber, qIndex, type, difficulty)
            "ja" -> buildJapaneseQuestion(id, levelNumber, qIndex, type, difficulty)
            "ko" -> buildKoreanQuestion(id, levelNumber, qIndex, type, difficulty)
            "zh" -> buildChineseQuestion(id, levelNumber, qIndex, type, difficulty)
            "en" -> buildEnglishQuestion(id, levelNumber, qIndex, type, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, lang)
        }
    }

    // --- BENGALI QUESTIONS ---
    private fun buildBengaliQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        val seed = (levelNumber * 13 + qIndex * 7) % 15

        return when (type) {
            QuestionType.RIDDLE -> when (seed % 4) {
                0 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "যার নাই কোনো হাত-পা, তবু সব জায়গায় ঘোরে — কী এটা?",
                    correctAnswer = "বাতাস", wrongAnswers = listOf("পানি", "আলো", "শব্দ"),
                    difficulty = difficulty, hint = "এটি চোখে দেখা যায় না কিন্তু অনুভব করা যায়।",
                    explanation = "বাতাসের কোনো নির্দিষ্ট আকার বা হাত-পা নেই, কিন্তু সর্বত্র প্রবাহিত হয়।"
                )
                1 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "মুখে খাবার থাকে কিন্তু সে খেতে পারে না — কী এটা?",
                    correctAnswer = "নদী", wrongAnswers = listOf("চামচ", "থালা", "পুকুর"),
                    difficulty = difficulty, hint = "প্রাকৃতিক জলের উৎস।",
                    explanation = "নদীর মুখ বা মোহনা থাকে কিন্তু সে কখনো খাবার গ্রহণ করে না।"
                )
                2 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "যত বেশি কাটবেন, তত বড় হবে — কী এটা?",
                    correctAnswer = "গর্ত", wrongAnswers = listOf("গাছ", "দড়ি", "কাগজ"),
                    difficulty = difficulty, hint = "মাটিতে খোঁড়া হয়।",
                    explanation = "গর্ত থেকে মাটি কাটলে গর্তের আকার বৃদ্ধি পায়।"
                )
                else -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "রাতে আসে কিন্তু দিনে গায়েব হয়ে যায় — কী এটা?",
                    correctAnswer = "তারা", wrongAnswers = listOf("সূর্য", "মেঘ", "পাখি"),
                    difficulty = difficulty, hint = "আকাশে ঝিলমিল করে।",
                    explanation = "দিনের আলোর কারণে রাতের তারাসমূহ অদৃশ্য হয়ে যায়।"
                )
            }

            QuestionType.SYNONYM_MEANING -> when (seed % 4) {
                0 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "'আকাশ' শব্দের সঠিক সমার্থক শব্দ কোনটি?",
                    correctAnswer = "গগন", wrongAnswers = listOf("অবনী", "পাবক", "সলিল"),
                    difficulty = difficulty, hint = "মহাকাশের প্রতিশব্দ।",
                    explanation = "গগন, অম্বরা, অন্তরীক্ষ হলো আকাশের সমার্থক শব্দ।"
                )
                1 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "'সূর্য' শব্দের সঠিক সমার্থক শব্দ চিহ্নিত করুন:",
                    correctAnswer = "রবি", wrongAnswers = listOf("শশী", "পবন", "গিরি"),
                    difficulty = difficulty, hint = "দিনের আলো প্রদানকারী।",
                    explanation = "রবি, ভানু, আদিত্য হলো সূর্যের সমার্থক শব্দ।"
                )
                2 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "'জল' শব্দের সমার্থক শব্দ কোনটি?",
                    correctAnswer = "পানি", wrongAnswers = listOf("অনল", "বায়ু", "ভূধর"),
                    difficulty = difficulty, hint = "জীবন ধারণের জন্য অপরিহার্য তরল।",
                    explanation = "জল, পানি, সলিল, বারি সমার্থক শব্দ।"
                )
                else -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "'অগ্নি' শব্দের সঠিক সমার্থক শব্দ কোনটি?",
                    correctAnswer = "অনল", wrongAnswers = listOf("পবন", "সুধা", "নিশি"),
                    difficulty = difficulty, hint = "উত্তাপ ও শিখা উৎপন্ন করে।",
                    explanation = "অনল, পাবক, আগুন হলো অগ্নির সমার্থক শব্দ।"
                )
            }

            QuestionType.MATH_LOGIC -> generateMathQuestion("bn", levelNumber, qIndex, difficulty)

            QuestionType.COUNTRY_TRIVIA -> when (seed % 4) {
                0 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "বাংলাদেশের জাতীয় স্তন্যপায়ী প্রাণী কোনটি?",
                    correctAnswer = "রয়েল বেঙ্গল টাইগার", wrongAnswers = listOf("হরিণ", "হাতি", "সিংহ"),
                    difficulty = difficulty, hint = "সুন্দরবনে বাস করে।",
                    explanation = "রয়েল বেঙ্গল টাইগার বাংলাদেশের জাতীয় প্রাণী।"
                )
                1 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "বাংলাদেশের জাতীয় স্মৃতিসৌধ কোথায় অবস্থিত?",
                    correctAnswer = "সাভার", wrongAnswers = listOf("মিরপুর", "গাজীপুর", "কুমিল্লা"),
                    difficulty = difficulty, hint = "ঢাকার নিকটবর্তী শিল্পাঞ্চল।",
                    explanation = "জাতীয় স্মৃতিসৌধ সাভারে অবস্থিত।"
                )
                2 -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "বাংলাদেশের দীর্ঘতম প্রাকৃতিক সমুদ্র সৈকত কোনটি?",
                    correctAnswer = "কক্সবাজার", wrongAnswers = listOf("কুয়াকাটা", "পতেঙ্গা", "সেন্টমার্টিন"),
                    difficulty = difficulty, hint = "বিশ্বের দীর্ঘতম বালুকাময় সৈকত।",
                    explanation = "কক্সবাজার সমুদ্র সৈকত পৃথিবীর দীর্ঘতম প্রাকৃতিক সমুদ্র সৈকত।"
                )
                else -> QuizQuestion(
                    id = id, language = "bn", type = type,
                    questionText = "বাংলাদেশের সংবিধান দিবস কবে পালিত হয়?",
                    correctAnswer = "৪ নভেম্বর", wrongAnswers = listOf("১৬ ডিসেম্বর", "২৬ মার্চ", "২১ ফেব্রুয়ারি"),
                    difficulty = difficulty, hint = "১৯৭২ সালের নভেম্বর মাস।",
                    explanation = "১৯৭২ সালের ৪ নভেম্বর গণপরিষদে সংবিধান গৃহীত হয়।"
                )
            }

            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "bn")
        }
    }

    // --- ENGLISH QUESTIONS ---
    private fun buildEnglishQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        val seed = (levelNumber * 13 + qIndex * 7) % 15

        return when (type) {
            QuestionType.RIDDLE -> when (seed % 3) {
                0 -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "I have hands, but I cannot clap. What am I?",
                    correctAnswer = "A Clock", wrongAnswers = listOf("A Robot", "A Mirror", "A Glove"),
                    difficulty = difficulty, hint = "It tells time.",
                    explanation = "A clock has hour and minute hands but no physical hands to clap."
                )
                1 -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "What gets wetter and wetter the more it dries?",
                    correctAnswer = "A Towel", wrongAnswers = listOf("A Sponge", "A Cloud", "Rain"),
                    difficulty = difficulty, hint = "Used after a shower.",
                    explanation = "A towel absorbs moisture from your body as it dries you."
                )
                else -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "What has keys, but no locks?",
                    correctAnswer = "A Piano", wrongAnswers = listOf("A Door", "A Safe", "A Car"),
                    difficulty = difficulty, hint = "A musical instrument.",
                    explanation = "A piano has 88 musical keys but no locks."
                )
            }

            QuestionType.SYNONYM_MEANING -> when (seed % 3) {
                0 -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "Which word is a synonym for 'Enormous'?",
                    correctAnswer = "Huge", wrongAnswers = listOf("Tiny", "Slender", "Quiet"),
                    difficulty = difficulty, hint = "Means very large.",
                    explanation = "Enormous and Huge both mean extremely large in size."
                )
                1 -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "Select the word closest in meaning to 'Rapid':",
                    correctAnswer = "Fast", wrongAnswers = listOf("Slow", "Heavy", "Dark"),
                    difficulty = difficulty, hint = "Moving with speed.",
                    explanation = "Rapid means happening in a brief time or moving fast."
                )
                else -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "What is a synonym for 'Courageous'?",
                    correctAnswer = "Brave", wrongAnswers = listOf("Timid", "Fearful", "Cautious"),
                    difficulty = difficulty, hint = "Not showing fear.",
                    explanation = "Courageous and Brave both denote readiness to face danger."
                )
            }

            QuestionType.MATH_LOGIC -> generateMathQuestion("en", levelNumber, qIndex, difficulty)

            QuestionType.COUNTRY_TRIVIA -> when (seed % 3) {
                0 -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "What is the capital city of the United States?",
                    correctAnswer = "Washington, D.C.", wrongAnswers = listOf("New York", "Los Angeles", "Chicago"),
                    difficulty = difficulty, hint = "Located on the Potomac River.",
                    explanation = "Washington, D.C. is the federal capital of the USA."
                )
                1 -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "Which statue stands on Liberty Island in New York Harbor?",
                    correctAnswer = "Statue of Liberty", wrongAnswers = listOf("Eiffel Tower", "Mount Rushmore", "Golden Gate"),
                    difficulty = difficulty, hint = "A gift from France.",
                    explanation = "The Statue of Liberty was dedicated in 1886 as a gift from France."
                )
                else -> QuizQuestion(
                    id = id, language = "en", type = type,
                    questionText = "Which ocean borders the west coast of the United States?",
                    correctAnswer = "Pacific Ocean", wrongAnswers = listOf("Atlantic Ocean", "Indian Ocean", "Arctic Ocean"),
                    difficulty = difficulty, hint = "The world's largest ocean.",
                    explanation = "The Pacific Ocean lies along the western coast of North America."
                )
            }

            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "en")
        }
    }

    // --- HINDI QUESTIONS ---
    private fun buildHindiQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        val seed = (levelNumber * 11 + qIndex * 5) % 10

        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> when (seed % 3) {
                0 -> QuizQuestion(
                    id = id, language = "hi", type = type,
                    questionText = "भारत की राजधानी कौन सी है?",
                    correctAnswer = "नई दिल्ली", wrongAnswers = listOf("मुंबई", "कोलकाता", "चेन्नई"),
                    difficulty = difficulty, hint = "उत्तर भारत में स्थित है।",
                    explanation = "नई दिल्ली भारत की आधिकारिक राजधानी है।"
                )
                1 -> QuizQuestion(
                    id = id, language = "hi", type = type,
                    questionText = "भारत की राष्ट्रीय नदी कौन सी है?",
                    correctAnswer = "गंगा", wrongAnswers = listOf("यमुना", "गोदावरी", "नर्मदा"),
                    difficulty = difficulty, hint = "हिमालय से निकलती है।",
                    explanation = "गंगा भारत की सबसे पवित्र और राष्ट्रीय नदी है।"
                )
                else -> QuizQuestion(
                    id = id, language = "hi", type = type,
                    questionText = "भारत का राष्ट्रीय पशु कौन सा है?",
                    correctAnswer = "बाघ", wrongAnswers = listOf("शेर", "हाथी", "हिरण"),
                    difficulty = difficulty, hint = "रॉयल बंगाल प्रजाति।",
                    explanation = "बाघ भारत का राष्ट्रीय पशु है।"
                )
            }
            QuestionType.MATH_LOGIC -> generateMathQuestion("hi", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "hi")
        }
    }

    // --- ARABIC QUESTIONS ---
    private fun buildArabicQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        val seed = (levelNumber * 11 + qIndex * 5) % 10

        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> when (seed % 3) {
                0 -> QuizQuestion(
                    id = id, language = "ar", type = type,
                    questionText = "ما هي عاصمة المملكة العربية السعودية؟",
                    correctAnswer = "الرياض", wrongAnswers = listOf("جدة", "مكة المكرمة", "الدمام"),
                    difficulty = difficulty, hint = "أكبر مدينة في المملكة.",
                    explanation = "الرياض هي عاصمة المملكة العربية السعودية."
                )
                1 -> QuizQuestion(
                    id = id, language = "ar", type = type,
                    questionText = "ما هو أطول نهر في العالم؟",
                    correctAnswer = "نهر النيل", wrongAnswers = listOf("نهر الأمازون", "نهر الفرات", "نهر دجلة"),
                    difficulty = difficulty, hint = "يمر في مصر والعديد من الدول الإفريقية.",
                    explanation = "نهر النيل يعتبر أطول نهر في العالم."
                )
                else -> QuizQuestion(
                    id = id, language = "ar", type = type,
                    questionText = "أين تقع الكعبة المشرفة؟",
                    correctAnswer = "مكة المكرمة", wrongAnswers = listOf("المدينة المنورة", "القدس", "القاهرة"),
                    difficulty = difficulty, hint = "قبلة المسلمين.",
                    explanation = "تقع الكعبة المشرفة في المسجد الحرام بمكة المكرمة."
                )
            }
            QuestionType.MATH_LOGIC -> generateMathQuestion("ar", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "ar")
        }
    }

    // --- SPANISH QUESTIONS ---
    private fun buildSpanishQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        val seed = (levelNumber * 11 + qIndex * 5) % 10

        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> QuizQuestion(
                id = id, language = "es", type = type,
                questionText = "¿Cuál es la capital de España?",
                correctAnswer = "Madrid", wrongAnswers = listOf("Barcelona", "Sevilla", "Valencia"),
                difficulty = difficulty, hint = "Ubicada en el centro de la península ibérica.",
                explanation = "Madrid es la capital y ciudad más grande de España."
            )
            QuestionType.MATH_LOGIC -> generateMathQuestion("es", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "es")
        }
    }

    // --- FRENCH QUESTIONS ---
    private fun buildFrenchQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> QuizQuestion(
                id = id, language = "fr", type = type,
                questionText = "Quelle est la capitale de la France ?",
                correctAnswer = "Paris", wrongAnswers = listOf("Lyon", "Marseille", "Nice"),
                difficulty = difficulty, hint = "Ville lumière et Tour Eiffel.",
                explanation = "Paris est la capitale de la République française."
            )
            QuestionType.MATH_LOGIC -> generateMathQuestion("fr", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "fr")
        }
    }

    // --- GERMAN QUESTIONS ---
    private fun buildGermanQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> QuizQuestion(
                id = id, language = "de", type = type,
                questionText = "Was ist die Hauptstadt von Deutschland?",
                correctAnswer = "Berlin", wrongAnswers = listOf("München", "Hamburg", "Frankfurt"),
                difficulty = difficulty, hint = "Bekannt für das Brandenburger Tor.",
                explanation = "Berlin ist die Bundeshauptstadt von Deutschland."
            )
            QuestionType.MATH_LOGIC -> generateMathQuestion("de", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "de")
        }
    }

    // --- JAPANESE QUESTIONS ---
    private fun buildJapaneseQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> QuizQuestion(
                id = id, language = "ja", type = type,
                questionText = "日本の首都はどこですか？",
                correctAnswer = "東京", wrongAnswers = listOf("大阪", "京都", "横浜"),
                difficulty = difficulty, hint = "スカイツリーがある大都市。",
                explanation = "日本の首都は東京です。"
            )
            QuestionType.MATH_LOGIC -> generateMathQuestion("ja", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "ja")
        }
    }

    // --- KOREAN QUESTIONS ---
    private fun buildKoreanQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> QuizQuestion(
                id = id, language = "ko", type = type,
                questionText = "대한민국의 수도는 어디인가요?",
                correctAnswer = "서울", wrongAnswers = listOf("부산", "인천", "대구"),
                difficulty = difficulty, hint = "한강이 흐르는 도시.",
                explanation = "대한민국의 수도는 서울특별시입니다."
            )
            QuestionType.MATH_LOGIC -> generateMathQuestion("ko", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "ko")
        }
    }

    // --- CHINESE QUESTIONS ---
    private fun buildChineseQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int
    ): QuizQuestion {
        return when (type) {
            QuestionType.COUNTRY_TRIVIA -> QuizQuestion(
                id = id, language = "zh", type = type,
                questionText = "中国的首都是哪里？",
                correctAnswer = "北京", wrongAnswers = listOf("上海", "广州", "深圳"),
                difficulty = difficulty, hint = "故宫和天安门所在地。",
                explanation = "北京是中华人民共和国的首都。"
            )
            QuestionType.MATH_LOGIC -> generateMathQuestion("zh", levelNumber, qIndex, difficulty)
            else -> buildUniversalQuestion(id, levelNumber, qIndex, type, difficulty, "zh")
        }
    }

    // --- INFINITE MATHEMATICAL & LOGIC GENERATOR (FOR ALL LANGUAGES) ---
    private fun generateMathQuestion(
        lang: String,
        levelNumber: Int,
        qIndex: Int,
        difficulty: Int
    ): QuizQuestion {
        val seedA = (levelNumber * 3 + qIndex * 2) * 5 + 12
        val seedB = (levelNumber * 2 + qIndex * 4) * 3 + 7

        val num1 = max(10, (seedA % (levelNumber * 10 + 20)))
        val num2 = max(5, (seedB % (levelNumber * 5 + 10)))
        val operationSeed = (levelNumber + qIndex) % 3

        val (qText, correct, wr1, wr2, wr3, hint, exp) = when (operationSeed) {
            0 -> {
                val ans = num1 + num2
                val question = when (lang) {
                    "bn" -> "$num1 + $num2 এর যোগফল কত?"
                    "hi" -> "$num1 + $num2 का योग क्या होगा?"
                    "ar" -> "ما هو حاصل جمع $num1 + $num2؟"
                    "es" -> "¿Cuánto es $num1 + $num2?"
                    "fr" -> "Combien font $num1 + $num2 ?"
                    "de" -> "Was ist $num1 + $num2?"
                    "ja" -> "$num1 + $num2 の合計はいくつですか？"
                    "ko" -> "$num1 + $num2 의 합은 얼마인가요?"
                    "zh" -> "$num1 + $num2 等于多少？"
                    else -> "What is the sum of $num1 + $num2?"
                }
                val expStr = when (lang) {
                    "bn" -> "$num1 এর সাথে $num2 যোগ করলে $ans হয়।"
                    "hi" -> "$num1 में $num2 जोड़ने पर $ans प्राप्त होता है।"
                    "ar" -> "جمع $num1 و $num2 يساوي $ans."
                    else -> "Adding $num1 and $num2 equals $ans."
                }
                Tuple7(question, "$ans", "${ans + 5}", "${ans - 3}", "${ans + 10}", "Math calculation.", expStr)
            }
            1 -> {
                val ans = num1 * num2
                val question = when (lang) {
                    "bn" -> "$num1 × $num2 এর গুণফল কত?"
                    "hi" -> "$num1 × $num2 का गुणनफल क्या है?"
                    "ar" -> "ما هو حاصل ضرب $num1 × $num2؟"
                    "es" -> "¿Cuánto es $num1 × $num2?"
                    "fr" -> "Combien font $num1 × $num2 ?"
                    "de" -> "Was ist $num1 × $num2?"
                    "ja" -> "$num1 × $num2 の積はいくつですか？"
                    "ko" -> "$num1 × $num2 의 곱은 얼마인가요?"
                    "zh" -> "$num1 × $num2 等于多少？"
                    else -> "What is the product of $num1 × $num2?"
                }
                val expStr = when (lang) {
                    "bn" -> "$num1 কে $num2 দ্বারা গুণ করলে $ans পাওয়া যায়।"
                    "hi" -> "$num1 को $num2 से गुणा करने पर $ans आता है।"
                    else -> "Multiplying $num1 by $num2 results in $ans."
                }
                Tuple7(question, "$ans", "${ans + num2}", "${ans - num1}", "${ans + 12}", "Multiplication.", expStr)
            }
            else -> {
                val sumVal = num1 + num2
                val question = when (lang) {
                    "bn" -> "যদি X + $num2 = $sumVal হয়, তবে X এর মান কত?"
                    "hi" -> "यदि X + $num2 = $sumVal है, तो X का मान क्या है?"
                    "ar" -> "إذا كان X + $num2 = $sumVal ، فما قيمة X؟"
                    "es" -> "Si X + $num2 = $sumVal, ¿cuál es el valor de X?"
                    "fr" -> "Si X + $num2 = $sumVal, quelle est la valeur de X ?"
                    "de" -> "Wenn X + $num2 = $sumVal, wie viel ist X?"
                    "ja" -> "X + $num2 = $sumVal のとき、X の値はいくつですか？"
                    "ko" -> "X + $num2 = $sumVal 일 때, X의 값은 얼마인가요?"
                    "zh" -> "如果 X + $num2 = $sumVal，那么 X 的值是多少？"
                    else -> "If X + $num2 = $sumVal, what is the value of X?"
                }
                val expStr = when (lang) {
                    "bn" -> "$sumVal থেকে $num2 বিয়োগ করলে X = $num1 পাওয়া যায়।"
                    else -> "Subtracting $num2 from $sumVal gives X = $num1."
                }
                Tuple7(question, "$num1", "${num1 + 2}", "${num1 - 2}", "${num1 + 5}", "Algebraic equation.", expStr)
            }
        }

        return QuizQuestion(
            id = "${lang}_L${levelNumber}_Q${qIndex}_math",
            language = lang,
            type = QuestionType.MATH_LOGIC,
            questionText = qText,
            correctAnswer = correct,
            wrongAnswers = listOf(wr1, wr2, wr3),
            difficulty = difficulty,
            hint = hint,
            explanation = exp
        )
    }

    // --- UNIVERSAL PROCEDURAL QUESTION GENERATOR (INFINITE LEVELS BACKUP) ---
    private fun buildUniversalQuestion(
        id: String,
        levelNumber: Int,
        qIndex: Int,
        type: QuestionType,
        difficulty: Int,
        lang: String
    ): QuizQuestion {
        val seed = (levelNumber * 17 + qIndex * 9) % 12

        return when (seed % 4) {
            0 -> QuizQuestion(
                id = id, language = lang, type = QuestionType.FACT_BASED,
                questionText = when (lang) {
                    "bn" -> "আমাদের সৌরজগতের বৃহত্তম গ্রহ কোনটি?"
                    "hi" -> "हमारे सौर मंडल का सबसे बड़ा ग्रह कौन सा है?"
                    "ar" -> "ما هو أكبر كوكب في نظامنا الشمسي؟"
                    "es" -> "¿Cuál es el planeta más grande de nuestro sistema solar?"
                    "fr" -> "Quelle est la plus grande planète de notre système solaire ?"
                    "de" -> "Was ist der größte Planet in unserem Sonnensystem?"
                    "ja" -> "太陽系で最も大きい惑星はどれですか？"
                    "ko" -> "태양계에서 가장 큰 행성은 무엇인가요?"
                    "zh" -> "太阳系中最大的行星是什么？"
                    else -> "Which is the largest planet in our solar system?"
                },
                correctAnswer = when (lang) {
                    "bn" -> "বৃহস্পতি"
                    "hi" -> "बृहस्पति"
                    "ar" -> "المشتري"
                    "es" -> "Júpiter"
                    "fr" -> "Jupiter"
                    "de" -> "Jupiter"
                    "ja" -> "木星"
                    "ko" -> "목성"
                    "zh" -> "木星"
                    else -> "Jupiter"
                },
                wrongAnswers = when (lang) {
                    "bn" -> listOf("মঙ্গল", "পৃথিবী", "শুক্র")
                    "hi" -> listOf("मंगल", "पृथ्वी", "शुक्र")
                    "ar" -> listOf("المريخ", "الأرض", "الزهرة")
                    "es" -> listOf("Marte", "Tierra", "Venus")
                    "fr" -> listOf("Mars", "Terre", "Vénus")
                    "de" -> listOf("Mars", "Erde", "Venus")
                    "ja" -> listOf("火星", "地球", "金星")
                    "ko" -> listOf("화성", "지구", "금성")
                    "zh" -> listOf("火星", "地球", "金星")
                    else -> listOf("Mars", "Earth", "Venus")
                },
                difficulty = difficulty,
                hint = "Jupiter",
                explanation = "Jupiter is the largest gas giant in the solar system."
            )

            1 -> QuizQuestion(
                id = id, language = lang, type = QuestionType.FILL_IN_BLANK,
                questionText = when (lang) {
                    "bn" -> "পানি সাধারণ চাপে ____ ডিগ্রি সেলসিয়াসে ফোটে।"
                    "hi" -> "पानी सामान्य दबाव पर ____ डिग्री सेल्सियस पर उबलता है।"
                    "ar" -> "يغلي الماء عند درجة حرارة ____ مئوية تحت الضغط الطبيعي."
                    "es" -> "El agua hierve a ____ grados Celsius bajo presión normal."
                    "fr" -> "L'eau bout à ____ degrés Celsius sous pression normale."
                    "de" -> "Wasser kocht unter Normaldruck bei ____ Grad Celsius."
                    "ja" -> "水は通常気圧の下で ____ 度で沸騰します。"
                    "ko" -> "물은 일반 기압에서 ____ 도에서 끓습니다."
                    "zh" -> "水在标准大气压下的沸点是 ____ 摄氏度。"
                    else -> "Water boils at ____ degrees Celsius under normal pressure."
                },
                correctAnswer = "100",
                wrongAnswers = listOf("50", "0", "120"),
                difficulty = difficulty,
                hint = "100°C",
                explanation = "Boiling point of water at sea level is 100°C."
            )

            2 -> generateMathQuestion(lang, levelNumber, qIndex, difficulty)

            else -> QuizQuestion(
                id = id, language = lang, type = QuestionType.FACT_BASED,
                questionText = when (lang) {
                    "bn" -> "পৃথিবীতে কয়টি মহাদেশ আছে?"
                    "hi" -> "पृथ्वी पर कितने महाद्वीप हैं?"
                    "ar" -> "كم عدد القارات في كوكب الأرض؟"
                    "es" -> "¿Cuántos continentes hay en la Tierra?"
                    "fr" -> "Combien y a-t-il de continents sur Terre ?"
                    "de" -> "Wie viele Kontinente gibt es auf der Erde?"
                    "ja" -> "地球にはいくつの大陸がありますか？"
                    "ko" -> "지구에는 몇 개의 대륙이 있나요?"
                    "zh" -> "地球上有多少个大洲？"
                    else -> "How many continents are there on Earth?"
                },
                correctAnswer = "7",
                wrongAnswers = listOf("5", "6", "8"),
                difficulty = difficulty,
                hint = "Asia, Africa, North America, South America, Antarctica, Europe, Australia.",
                explanation = "There are 7 conventional continents."
            )
        }
    }

    private data class Tuple7<T1, T2, T3, T4, T5, T6, T7>(
        val val1: T1, val val2: T2, val val3: T3, val val4: T4, val val5: T5, val val6: T6, val val7: T7
    )
}
