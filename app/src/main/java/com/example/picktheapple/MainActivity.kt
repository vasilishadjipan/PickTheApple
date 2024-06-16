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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val playerCord = Cordinates(5, 5)
    var appleCord = Cordinates(0, 0)
    var ghostsCord: MutableList<Cordinates> = mutableListOf()
    var score = 0
    var activeGame = true
    var screenWidth = 0

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
        setImageOnBoard(playerCord.x, playerCord.y, R.drawable.happy_emoji)
        activeGame = true
        ghostsCord = mutableListOf()
        score = 0
        scoreText.text = "0"
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


        CoroutineScope(Dispatchers.IO).launch {
            // Your long-running task here
            // val result = performTask()
            while (activeGame) {
                // Switch to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    // Update UI elements here
                    moveGhosts()
                    if (checkGhostsCord(playerCord)) gameOver()
                }
                delay(1000)
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
            && randNum / 11 == appleCord.x && randNum % 11 == appleCord.y
            && checkGhostsCord(Cordinates(randNum / 11, randNum % 11))
        ) {
            randNum = (Math.random() * 121).toInt()
        }
        val x = randNum / 11
        val y = randNum % 11
        ghostsCord.add(Cordinates(x, y))
        setImageOnBoard(x, y, R.drawable.ghost)
    }

    private fun MainView.moveGhosts() = apply {
        var moved = false
        for (ghost in ghostsCord) {
            do {
                val randNum = (Math.random() * 4).toInt()
                when (randNum) {
                    0 -> {
                        if (checkBounds(ghost.x - 1, ghost.y)
                            && ghost.x - 1 != appleCord.x && ghost.y != appleCord.y
                            && !checkGhostsCord(Cordinates(ghost.x - 1, ghost.y))
                        ) {
                            setImageOnBoard(ghost.x, ghost.y, null)
                            ghost.x -= 1
                            setImageOnBoard(ghost.x, ghost.y, R.drawable.ghost)
                            moved = true
                        }
                    }

                    1 -> {
                        if (checkBounds(ghost.x + 1, ghost.y)
                            && ghost.x + 1 != appleCord.x && ghost.y != appleCord.y
                            && !checkGhostsCord(Cordinates(ghost.x + 1, ghost.y))
                        ) {
                            setImageOnBoard(ghost.x, ghost.y, null)
                            ghost.x += 1
                            setImageOnBoard(ghost.x, ghost.y, R.drawable.ghost)
                            moved = true
                        }
                    }

                    2 -> {
                        if (checkBounds(ghost.x, ghost.y - 1)
                            && ghost.x != appleCord.x && ghost.y - 1 != appleCord.y
                            && !checkGhostsCord(Cordinates(ghost.x, ghost.y - 1))
                        ) {
                            setImageOnBoard(ghost.x, ghost.y, null)
                            ghost.y -= 1
                            setImageOnBoard(ghost.x, ghost.y, R.drawable.ghost)
                            moved = true
                        }
                    }

                    else -> {
                        if (checkBounds(ghost.x, ghost.y + 1)
                            && ghost.x != appleCord.x && ghost.y != appleCord.y + 1
                            && !checkGhostsCord(Cordinates(ghost.x, ghost.y + 1))
                        ) {
                            setImageOnBoard(ghost.x, ghost.y, null)
                            ghost.y += 1
                            setImageOnBoard(ghost.x, ghost.y, R.drawable.ghost)
                            moved = true
                        }
                    }
                }
            } while (!moved)
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

    fun MainView.gameOver() {
        activeGame = false
        for (row in board) {
            for (image in row) {
                image.setImageDrawable(null)
            }
        }
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