package com.tomas.vocabquiztest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_play.*
import java.util.*
import kotlin.concurrent.schedule


class PlayActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var vocabMap = Dictionary().vocabMap
    private var correctButton: Int = 0
    private lateinit var tts: TextToSpeech
    private lateinit var countDownTimer: CountDownTimer
    private var millis: Long = 30999
    private var points: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val playerId = intent.getStringExtra("playerId")
        val documentId = intent.getStringExtra("document")

        if(playerId != null && documentId != null) {
            val docRef = db.collection(playerId).document(documentId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        vocabMap = document.data as Map<String, String>
                    } else {
                    }
                }
                .addOnFailureListener { exception ->
                }
        }

        pointsText.text = "Points: $points"

        startTimer()

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
                optionAbutton.background = getDrawable(R.drawable.gradientgreen)
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionAbutton.background = getDrawable(R.drawable.gradientred)
                updateTimer(-5000)
            }
        }

        optionBbutton.setOnClickListener {
            optionBbutton.isEnabled = false
            if (correctButton == 2){
                disableButtons()
                tts.speak(optionBbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionBbutton.background = getDrawable(R.drawable.gradientgreen)
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionBbutton.background = getDrawable(R.drawable.gradientred)
                updateTimer(-5000)
            }
        }

        optionCbutton.setOnClickListener {
            optionCbutton.isEnabled = false
            if (correctButton == 3){
                disableButtons()
                tts.speak(optionCbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionCbutton.background = getDrawable(R.drawable.gradientgreen)
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionCbutton.background = getDrawable(R.drawable.gradientred)
                updateTimer(-5000)
            }
        }

        optionDbutton.setOnClickListener {
            optionDbutton.isEnabled = false
            if (correctButton == 4){
                disableButtons()
                tts.speak(optionDbutton.text.toString(), TextToSpeech.QUEUE_ADD, null,null)
                optionDbutton.background = getDrawable(R.drawable.gradientgreen)
                points++
                updateTimer(2000)
                Timer("Delay", false).schedule(1000) {
                    createNewQuestion()
                }
            }
            else {
                optionDbutton.background = getDrawable(R.drawable.gradientred)
                updateTimer(-5000)
            }
        }

        restartButton.setOnClickListener {
            countDownTimer.cancel()
            points = 0
            pointsText.text = "Points: $points"
            millis = 30999
            createNewQuestion()
            startTimer()
        }
    }


    private fun createNewQuestion() {
        enableButtons()
        optionAbutton.background = getDrawable(R.drawable.gradientlight)
        optionBbutton.background = getDrawable(R.drawable.gradientlight)
        optionCbutton.background = getDrawable(R.drawable.gradientlight)
        optionDbutton.background = getDrawable(R.drawable.gradientlight)

        val random = Random()
        val question = vocabMap.entries.elementAt(random.nextInt(vocabMap.size))
        val correctAnswer = question.key
        var tempVocabMap = vocabMap.toMutableMap()
        tempVocabMap.remove(question.key)
        val option1 = tempVocabMap.entries.elementAt(random.nextInt(tempVocabMap.size))
        tempVocabMap.remove(option1.key)
        val option2 = tempVocabMap.entries.elementAt(random.nextInt(tempVocabMap.size))
        tempVocabMap.remove(option2.key)
        val option3 = tempVocabMap.entries.elementAt(random.nextInt(tempVocabMap.size))

        this.questionText.text = question.value

        correctButton = random.nextInt(4) + 1

        when(correctButton) {
            1 -> {
                this.optionAbutton.text = correctAnswer
                this.optionBbutton.text = option1.key
                this.optionCbutton.text = option2.key
                this.optionDbutton.text = option3.key
            }
            2 -> {
                this.optionAbutton.text = option1.key
                this.optionBbutton.text = correctAnswer
                this.optionCbutton.text = option2.key
                this.optionDbutton.text = option3.key
            }
            3 -> {
                this.optionAbutton.text = option1.key
                this.optionBbutton.text = option2.key
                this.optionCbutton.text = correctAnswer
                this.optionDbutton.text = option3.key
            }
            4 -> {
                this.optionAbutton.text = option1.key
                this.optionBbutton.text = option2.key
                this.optionCbutton.text = option3.key
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

    private fun startTimer() {
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
    }
}
