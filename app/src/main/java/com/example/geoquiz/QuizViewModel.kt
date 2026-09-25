package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle

private const val TAG = "QuizViewModel"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"
class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var currentIndex = 0
    var correctQuestions = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
    var isCheater: Boolean
        get() = (savedStateHandle.get(IS_CHEATER_KEY) ?: false) as Boolean
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)
}