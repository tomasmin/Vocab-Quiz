package com.tomas.vocabquiztest

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_play.*
import java.util.*
import kotlin.concurrent.schedule
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class PlayActivity : AppCompatActivity() {

    private val vocabMap = Dictionary().vocabMap
    private var correctButton: Int = 0
    private lateinit var tts: TextToSpeech
    private lateinit var countDownTimer: CountDownTimer
    private var millis: Long = 30000
    private var points: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        pointsText.text = "Points: $points"


        countDownTimer = object: CountDownTimer(millis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerText.text = ("Time: " + millisUntilFinished / 1000)
                millis = millisUntilFinished
            }

            override fun onFinish() {
                disableButtons()
                val intent = Intent(this@PlayActivity, GameOverActivity::class.java)
                intent.putExtra("Points",points)
                startActivity(intent)
                finish()
            }
        }.start()

        createNewQuestion()

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR){
                tts.language = Locale.FRANCE
            }
        })

        optionAbutton.setOnClickListener {
            optionAbutton.isEnabled = false
            if (correctButton == 1){
                disableButtons()
                tts.speak(optionAbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionAbutton.setBackgroundColor(getColor(R.color.darkGreen))
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionAbutton.setBackgroundColor(Color.RED)
                updateTimer(-5000)
            }
        }

        optionBbutton.setOnClickListener {
            optionBbutton.isEnabled = false
            if (correctButton == 2){
                disableButtons()
                tts.speak(optionBbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionBbutton.setBackgroundColor(getColor(R.color.darkGreen))
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionBbutton.setBackgroundColor(Color.RED)
                updateTimer(-5000)
            }
        }

        optionCbutton.setOnClickListener {
            optionCbutton.isEnabled = false
            if (correctButton == 3){
                disableButtons()
                tts.speak(optionCbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionCbutton.setBackgroundColor(getColor(R.color.darkGreen))
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionCbutton.setBackgroundColor(Color.RED)
                updateTimer(-5000)
            }
        }

        optionDbutton.setOnClickListener {
            optionDbutton.isEnabled = false
            if (correctButton == 4){
                disableButtons()
                tts.speak(optionDbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionDbutton.setBackgroundColor(getColor(R.color.darkGreen))
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionDbutton.setBackgroundColor(Color.RED)
                updateTimer(-5000)
            }
        }

        restartButton.setOnClickListener {
            countDownTimer.cancel()
            recreate()
        }
    }


    private fun createNewQuestion() {
        enableButtons()
        optionAbutton.setBackgroundColor(Color.GRAY)
        optionBbutton.setBackgroundColor(Color.GRAY)
        optionCbutton.setBackgroundColor(Color.GRAY)
        optionDbutton.setBackgroundColor(Color.GRAY)

        val random = Random()
        val question = vocabMap.entries.elementAt(random.nextInt(vocabMap.size))
        val correctAnswer = question.value
        var tempVocabMap = vocabMap.toMutableMap()
        tempVocabMap.remove(question.key)
        val option1 = tempVocabMap.entries.elementAt(random.nextInt(tempVocabMap.size))
        tempVocabMap.remove(option1.key)
        val option2 = tempVocabMap.entries.elementAt(random.nextInt(tempVocabMap.size))
        tempVocabMap.remove(option2.key)
        val option3 = tempVocabMap.entries.elementAt(random.nextInt(tempVocabMap.size))

        this.questionText.text = question.key

        correctButton = random.nextInt(4) + 1

        when(correctButton) {
            1 -> {
                this.optionAbutton.text = correctAnswer
                this.optionBbutton.text = option1.value
                this.optionCbutton.text = option2.value
                this.optionDbutton.text = option3.value
            }
            2 -> {
                this.optionAbutton.text = option1.value
                this.optionBbutton.text = correctAnswer
                this.optionCbutton.text = option2.value
                this.optionDbutton.text = option3.value
            }
            3 -> {
                this.optionAbutton.text = option1.value
                this.optionBbutton.text = option2.value
                this.optionCbutton.text = correctAnswer
                this.optionDbutton.text = option3.value
            }
            4 -> {
                this.optionAbutton.text = option1.value
                this.optionBbutton.text = option2.value
                this.optionCbutton.text = option3.value
                this.optionDbutton.text = correctAnswer
            }
        }
    }

    private fun updateTimer(extraMillis: Long) {

        pointsText.text = "Points: $points"

        countDownTimer.cancel()

        countDownTimer = object: CountDownTimer(millis+extraMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timerText.text = ("Time: " + millisUntilFinished / 1000)
                millis = millisUntilFinished
            }

            override fun onFinish() {
                disableButtons()
                val intent = Intent(this@PlayActivity, GameOverActivity::class.java)
                intent.putExtra("Points",points)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    private fun disableButtons() {
        optionAbutton.isEnabled = false
        optionBbutton.isEnabled = false
        optionCbutton.isEnabled = false
        optionDbutton.isEnabled = false
    }

    private fun enableButtons() = this@PlayActivity.runOnUiThread {
        optionAbutton.isEnabled = true
        optionBbutton.isEnabled = true
        optionCbutton.isEnabled = true
        optionDbutton.isEnabled = true
    }


    override fun onBackPressed() {

        countDownTimer.cancel()

        super.onBackPressed()
    }
}
