package com.example.geoquiz

import android.app.Activity
import android.os.Bundle
import android.view.View
//import android.widget.Button
import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
//import com.example.geoquiz.Question
import com.example.geoquiz.databinding.ActivityMainBinding
//import android.util.Log
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            quizViewModel.questionBank[quizViewModel.currentIndex].cheater = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateQuestion()

        binding.questionTextView.setOnClickListener {view:View ->
            quizViewModel.currentIndex = (quizViewModel.currentIndex + 1)
            if (quizViewModel.currentIndex >= quizViewModel.questionBank.size) {
                showScore()
            } else {
                updateQuestion()
            }
        }

        binding.trueButton.setOnClickListener { view: View ->
            quizViewModel.questionBank[quizViewModel.currentIndex].wasCorrect = checkAnswer(true)
        }

        binding.falseButton.setOnClickListener { view: View ->
            quizViewModel.questionBank[quizViewModel.currentIndex].wasCorrect = checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.currentIndex = (quizViewModel.currentIndex + 1)
            if (quizViewModel.currentIndex >= quizViewModel.questionBank.size) {
                showScore()
            } else {
                updateQuestion()
            }
        }

        binding.previousButton.setOnClickListener {
            if (quizViewModel.currentIndex > 0) {
                quizViewModel.currentIndex = (quizViewModel.currentIndex - 1) % quizViewModel.questionBank.size
            } else {
                quizViewModel.currentIndex = 0
            }
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }
    }
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.questionBank[quizViewModel.currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)


        if (quizViewModel.questionBank[quizViewModel.currentIndex].wasCorrect == null) {
            binding.falseButton.visibility = View.VISIBLE
            binding.trueButton.visibility = View.VISIBLE
        } else {
            binding.falseButton.visibility = View.GONE
            binding.trueButton.visibility = View.GONE
        }
    }

    private fun checkAnswer(userAnswer: Boolean): Boolean {
        val correctAnswer = quizViewModel.questionBank[quizViewModel.currentIndex].answer
        var isCorrect = false
        if (userAnswer == correctAnswer) {
            isCorrect = true
        }
        val isCheater = quizViewModel.questionBank[quizViewModel.currentIndex].cheater
        val messageResId = when {
            isCheater == true -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Snackbar.make(binding.root, messageResId, Snackbar.LENGTH_SHORT).show()


        binding.falseButton.visibility = View.GONE
        binding.trueButton.visibility = View.GONE


        if (isCorrect && isCheater == false) quizViewModel.correctQuestions += 1

        return isCorrect

    }

    private fun showScore() {
        val scorePercent = (quizViewModel.correctQuestions * 100) / quizViewModel.questionBank.size
        Toast.makeText(this, "Your score: $scorePercent%" , Toast.LENGTH_SHORT).show()
    }

}