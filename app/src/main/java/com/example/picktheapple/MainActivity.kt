package com.example.picktheapple

import android.content.pm.ActivityInfo
import android.graphics.Color.BLUE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.GridLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    val playerCord = Cordinates(5, 5)
    var appleCord = Cordinates(0, 0)
    var ghostsCord: MutableList<Cordinates> = mutableListOf()
    var score = 0
    var activeGame = true
    var screenWidth = 0
    var ghostMoveJob: Job? = null // To track the coroutine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        val mainView = MainView(applicationContext)
        setContentView(mainView)
        mainView.drawGrid()
        mainView.play()

    }

    private fun MainView.play() = apply {
        activeGame = true
        ghostsCord = mutableListOf()
        score = 0
        scoreText.text = "0"
        initPlayer()
        placeApple()
        initGhost()
        joystick.upBtn.setOnClickListener {
            if (checkBounds(playerCord.x - 1, playerCord.y)) {
                setImageOnBoard(playerCord.x, playerCord.y, null)
                playerCord.x -= 1
                setImageOnBoard(playerCord.x, playerCord.y, R.drawable.happy_emoji)
                checkUpdateScore()
                if (checkGhostsCord(playerCord)) gameOver()
            }
        }
        joystick.downBtn.setOnClickListener {
            if (checkBounds(playerCord.x + 1, playerCord.y)) {
                setImageOnBoard(playerCord.x, playerCord.y, null)
                playerCord.x += 1
                setImageOnBoard(playerCord.x, playerCord.y, R.drawable.happy_emoji)
                checkUpdateScore()
                if (checkGhostsCord(playerCord)) gameOver()
            }
        }
        joystick.rightBtn.setOnClickListener {
            if (checkBounds(playerCord.x, playerCord.y + 1)) {
                setImageOnBoard(playerCord.x, playerCord.y, null)
                playerCord.y += 1
                setImageOnBoard(playerCord.x, playerCord.y, R.drawable.happy_emoji)
                checkUpdateScore()
                if (checkGhostsCord(playerCord)) gameOver()
            }
        }
        joystick.leftBtn.setOnClickListener {
            if (checkBounds(playerCord.x, playerCord.y - 1)) {
                setImageOnBoard(playerCord.x, playerCord.y, null)
                playerCord.y -= 1
                setImageOnBoard(playerCord.x, playerCord.y, R.drawable.happy_emoji)
                checkUpdateScore()
                if (checkGhostsCord(playerCord)) gameOver()
            }
        }


        ghostMoveJob = CoroutineScope(Dispatchers.Main).launch {
            while (activeGame) {
                // Update UI elements here on the Main thread
                moveGhosts()
                if (checkGhostsCord(playerCord))
                    gameOver()
                delay(1000) // Suspend coroutine for 1 second
            }
        }
    }

    private fun checkBounds(x: Int, y: Int): Boolean {
        return x > -1 && x < 11 && y > -1 && y < 11
    }

    private fun MainView.drawGrid() = apply {
        board.forEachIndexed { i, row ->
            row.forEachIndexed { j, col ->
                gridLayout.addView(col)
                col.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = (screenWidth - 112) / 11
                    height = (screenWidth - 112) / 11
                    setGravity(Gravity.CENTER)
                    val right = if (j != 10) 8 else 0
                    val bottom = if (i != 10) 8 else 0
                    setMargins(0, 0, right, bottom)
                }
                col.setBackgroundColor(BLUE)
            }
        }
    }

    private fun MainView.placeApple() = apply {
        var randNum = (Math.random() * 121).toInt()
        while (randNum / 11 == playerCord.x && randNum % 11 == playerCord.y
            && checkGhostsCord(Cordinates(randNum / 11, randNum % 11))
        ) {
            randNum = (Math.random() * 121).toInt()
        }
        appleCord.x = randNum / 11
        appleCord.y = randNum % 11
        setImageOnBoard(appleCord.x, appleCord.y, R.drawable.apple)
    }

    private fun MainView.initGhost() = apply {
        var randNum = (Math.random() * 121).toInt()
        while (randNum / 11 == playerCord.x && randNum % 11 == playerCord.y
            && isAppleCord(randNum / 11, randNum % 11)
            && checkGhostsCord(Cordinates(randNum / 11, randNum % 11))
        ) {
            randNum = (Math.random() * 121).toInt()
        }
        val x = randNum / 11
        val y = randNum % 11
        ghostsCord.add(Cordinates(x, y))
        setImageOnBoard(x, y, R.drawable.ghost)
    }


    private fun MainView.initPlayer() {
        playerCord.x = 5
        playerCord.y = 5
        setImageOnBoard(playerCord.x, playerCord.y, R.drawable.happy_emoji)
    }

    private fun MainView.moveGhosts() = apply {
        for (ghost in ghostsCord) {
            var bestMove = 0
            var bestMoveDistance = Float.MAX_VALUE
            var distance = 0f
            if (checkBounds(ghost.x - 1, ghost.y)
                && !isAppleCord(ghost.x - 1, ghost.y)
                && !checkGhostsCord(Cordinates(ghost.x - 1, ghost.y))
            ) {
                distance = findGhostPlayerDistance(ghost.x - 1, ghost.y)
                if(distance < bestMoveDistance) {
                    bestMove = 0
                    bestMoveDistance = distance
                }
            }
            if (checkBounds(ghost.x + 1, ghost.y)
              && !isAppleCord(ghost.x + 1, ghost.y)
              && !checkGhostsCord(Cordinates(ghost.x + 1, ghost.y))
            ) {
                distance = findGhostPlayerDistance(ghost.x + 1, ghost.y)
                if(distance < bestMoveDistance) {
                    bestMove = 1
                    bestMoveDistance = distance
                }
            }
            if (checkBounds(ghost.x, ghost.y - 1)
             && !isAppleCord(ghost.x, ghost.y - 1)
              && !checkGhostsCord(Cordinates(ghost.x, ghost.y - 1))
            ) {
                distance = findGhostPlayerDistance(ghost.x, ghost.y - 1)
                if(distance < bestMoveDistance) {
                    bestMove = 2
                    bestMoveDistance = distance
                }
            }
            if (checkBounds(ghost.x, ghost.y + 1)
               && !isAppleCord(ghost.x, ghost.y + 1)
                && !checkGhostsCord(Cordinates(ghost.x, ghost.y + 1))
            ) {
                distance = findGhostPlayerDistance(ghost.x, ghost.y + 1)
                if(distance < bestMoveDistance) {
                    bestMove = 3
                    bestMoveDistance = distance
                }
            }
            if (bestMoveDistance != Float.MAX_VALUE) {
                when (bestMove) {
                    0 -> updateGhostPosition(ghost, ghost.x - 1, ghost.y)
                    1 -> updateGhostPosition(ghost, ghost.x + 1, ghost.y)
                    2 -> updateGhostPosition(ghost, ghost.x, ghost.y - 1)
                    else -> updateGhostPosition(ghost, ghost.x, ghost.y + 1)
                }
            }
        }
    }

    private fun MainView.setImageOnBoard(x: Int, y: Int, @DrawableRes drawRes: Int?) {
        if (drawRes != null) {
            board[x][y].setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    drawRes
                )
            )
        } else board[x][y].setImageDrawable(null)
    }

    private fun MainView.checkUpdateScore() {
        if (playerCord.x == appleCord.x && playerCord.y == appleCord.y) {
            score++
            if (score % 10 == 0) initGhost()
            scoreText.text = score.toString()
            placeApple()
        }
    }

    fun checkGhostsCord(cord: Cordinates): Boolean {
        var ghostFound = false
        for (i in ghostsCord) {
            if (i.x == cord.x && i.y == cord.y) {
                ghostFound = true
                break
            }
        }
        return ghostFound
    }

    fun isAppleCord(x: Int, y: Int) = appleCord.x == x && appleCord.y == y

    fun findGhostPlayerDistance(ghostX: Int, ghostY: Int) =
        sqrt((ghostX - playerCord.x).toFloat().pow(2)
                + (ghostY - playerCord.y).toFloat().pow(2))


    private fun MainView.updateGhostPosition(ghost: Cordinates, newX: Int, newY: Int) {
        // Clear current position
        setImageOnBoard(ghost.x, ghost.y, null)
        // Update the ghost's coordinates
        ghost.x = newX
        ghost.y = newY
        // Set new position
        setImageOnBoard(ghost.x, ghost.y, R.drawable.ghost)
    }

    fun MainView.gameOver() {
        joystick.upBtn.setOnClickListener(null)
        joystick.downBtn.setOnClickListener(null)
        joystick.leftBtn.setOnClickListener(null)
        joystick.rightBtn.setOnClickListener(null)

        activeGame = false
        for (row in board) {
            for (image in row) {
                image.setImageDrawable(null)
            }
        }
        ghostMoveJob?.cancel()
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Game over")
        builder.setMessage("You score $score points")
        builder.setPositiveButton("Play Again") { dialog, which ->
            play()
        }
        builder.setNegativeButton("Exit") { dialog, which ->
            finish()
        }
        val dialog = builder.create()
        dialog.setCancelable(false) // Prevent closing on back button
        dialog.setCanceledOnTouchOutside(false) // Prevent closing on outside touch
        dialog.show()
    }

}