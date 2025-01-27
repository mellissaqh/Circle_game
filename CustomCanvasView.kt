package com.example.click_ball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import kotlin.math.pow

data class Circle(val x: Float, val y: Float, val radius: Float, val color: Int)

class CustomCanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val circles = mutableListOf<Circle>()
    private val random = java.util.Random()

    private val handler : Handler = Handler(Looper.getMainLooper())
    private val difficultyHandler: Handler = Handler(Looper.getMainLooper())
    private var spawnInterval: Long = 500
    private var dynamicSpawnInterval: Long = spawnInterval
    private val lifespan: Long = 3000

    private var score: Int = 0
    private var onScoreChangeListener: ((Int) -> Unit)? = null
    var onGameOverListener: ((Int) -> Unit)? = null
    private var onLivesChangeListener: ((Int) -> Unit)? = null
    private var lives: Int = 3
    private var gameOver = false

    private val colors = listOf(
        Color.GREEN,
        Color.BLUE,
        Color.RED
    )

    private var isSpawning = false


    fun setOnScoreChangeListener(listener: (Int) -> Unit){
        onScoreChangeListener = listener
    }

    fun setOnLivesChangeListener(listener: (Int) -> Unit){
        onLivesChangeListener = listener
    }

    fun startSpawning(){
        if (gameOver || isSpawning) return
        isSpawning = true

        handler.post(object: Runnable{
            override fun run() {
                addRandomCircle()
                invalidate()
                if (!gameOver){
                    handler.postDelayed(this, dynamicSpawnInterval)
                }
            }
        })

        difficultyHandler.postDelayed(object: Runnable{
            override fun run() {
                if(!gameOver){
                    dynamicSpawnInterval = (dynamicSpawnInterval * 0.9).toLong()
                    if(dynamicSpawnInterval < 50){
                        dynamicSpawnInterval = 50
                    }
                    difficultyHandler.postDelayed(this, 5000)
              }
            }
        }, 5000)
    }

    fun startRemoving(){
        if (gameOver) return
        handler.post(object: Runnable{
            override fun run(){
                removeCircle()
                invalidate()
                handler.postDelayed(this, lifespan)
            }
        })
    }

    fun addRandomCircle() {
        val radius = (60..120).random().toFloat()
        val x = random.nextFloat() * (width - 2 * radius) + radius
        val y = random.nextFloat() * (height - 2 * radius) + radius
        val color = colors.random()
        val newCircle = Circle(
            x = x,
            y = y,
            radius = radius,
            color = color
        )
        circles.add(newCircle)
    }

    fun removeCircle() {
        if (circles.isNotEmpty()){
            circles.removeAt(0)
            invalidate()
        }
    }


    //Här ritas cirklarna upp på canvasen
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (gameOver) return
        for (circle in circles) {
            val paint = Paint().apply {
                color = circle.color
                style = Paint.Style.FILL
            }
            canvas.drawCircle(circle.x, circle.y, circle.radius, paint)
        }
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_DOWN){
            val touchedX = event.x
            val touchedY = event.y

            for (i in circles.size - 1 downTo 0){
                val circle = circles[i]
                val distance = Math.sqrt(
                    ((circle.x - touchedX).toDouble().pow(2.0)) + ((circle.y - touchedY).toDouble().pow(2.0))
                )

                if (distance <= circle.radius){
                    circles.removeAt(i)
                    invalidate()

                    if (circle.color == Color.RED){
                        lives--
                    } else {
                        score++
                    }
                    onScoreChangeListener?.invoke(score)
                    onLivesChangeListener?.invoke(lives)

                    if(lives <= 0){
                        gameOver = true
                        onGameOverListener?.invoke(score)
                    }

                    break
                }
            }
        }
        return true
    }

    fun resetGame(){
        lives = 3
        score = 0
        gameOver = false
        circles.clear()
        isSpawning = false
        dynamicSpawnInterval = spawnInterval
        onScoreChangeListener?.invoke(score)
        onLivesChangeListener?.invoke(lives)
        invalidate()
    }

    fun stopHandlers() {
        handler.removeCallbacksAndMessages(null)
        difficultyHandler.removeCallbacksAndMessages(null)
    }
}