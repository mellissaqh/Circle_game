package com.example.click_ball

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class TimeActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    private var score = 0
    private var lives = 3
    private var scoreTitle: TextView? = null
    private var livesTitle: TextView? = null
    private var timerTitle: TextView? = null
    private var timer: CountDownTimer? = null
    private val initialTime: Long = 30000
    private var timeLeft: Long = initialTime
    private var canvasView: CustomCanvasView? = null
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        playBackgroundMusic()

        val startButton: Button = findViewById(R.id.btn_start)
        val backButton: Button = findViewById(R.id.btn_back)
        canvasView = findViewById(R.id.customCanvasView)
        val gameTitle: TextView = findViewById(R.id.game_title)
        scoreTitle = findViewById(R.id.score)
        livesTitle = findViewById(R.id.lives)
        timerTitle = findViewById(R.id.timer)
        gameTitle.text = "Time"

        backButton.setOnClickListener{
            finish()
        }

        startButton.setOnClickListener{
            startGame()
        }

        canvasView?.setOnScoreChangeListener {newScore ->
            score = newScore
            scoreTitle?.text = "Score: $score"
        }

        canvasView?.setOnLivesChangeListener { newLives ->
            lives = newLives
            livesTitle?.text = "Lives: $lives"
            if (lives <= 0 && !gameOver) {
                gameOver = true
                showGameOverDialog("Du förlorade tre liv. Försök igen!")
            }
        }

    }

    private fun playBackgroundMusic(){
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun startGame(){
        canvasView?.resetGame()
        gameOver = false
        resetTimer()
        canvasView?.startSpawning()
        canvasView?.startRemoving()
        startTimer()
    }

    private fun startTimer(){
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long){
                timeLeft = millisUntilFinished
                val secondsLeft = millisUntilFinished / 1000
                timerTitle?.text = "Time: $secondsLeft s"
            }

            override fun onFinish(){
                if (!gameOver) {
                    gameOver = true
                    showGameOverDialog("Time's up! You got $score points.")
                }
            }
        }.start()
    }

    private fun resetTimer() {
        timer?.cancel()
        timeLeft = initialTime
        timerTitle?.text = "Time: ${timeLeft / 1000} s"
    }

    private fun showGameOverDialog(message: String) {
        timer?.cancel()
        canvasView?.stopHandlers()
        val dialog = AlertDialog.Builder(this)
            .setTitle("Game finnished")
            .setMessage(message)
            .setPositiveButton("Try Again") { dialog, _ ->
                startGame()
                dialog.dismiss()
            }
            .setNegativeButton("Back to menu") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
        canvasView?.stopHandlers()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        gameOver = true
    }
}