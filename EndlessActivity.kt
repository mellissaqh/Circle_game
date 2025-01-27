package com.example.click_ball

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EndlessActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    private var score = 0
    private var lives = 3
    private var scoreTitle: TextView? = null
    private var livesTitle: TextView? = null
    private var canvasView: CustomCanvasView? = null

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
        gameTitle.text = "Endless"

        startButton.setOnClickListener{
            startGame()
        }

        backButton.setOnClickListener{
            finish()
        }

        canvasView?.setOnScoreChangeListener {newScore ->
            score = newScore
            scoreTitle?.text = "Score: $score"
        }

        canvasView?.onGameOverListener = { score ->
            showGameOverDialog(score)
        }

        canvasView?.setOnLivesChangeListener { newLives ->
            lives = newLives
            livesTitle?.text = "Lives: $lives"
        }

    }

    private fun playBackgroundMusic(){
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music_2)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun startGame(){
        canvasView?.resetGame()
        canvasView?.startSpawning()
        canvasView?.startRemoving()
    }

    private fun showGameOverDialog(score: Int){
        val dialog = AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Score: $score")
            .setPositiveButton("Try Again") {
                dialog, _ ->
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
        canvasView?.stopHandlers()
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

}