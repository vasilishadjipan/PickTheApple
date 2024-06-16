package com.example.picktheapple

import android.content.Context
import android.graphics.Color.BLACK
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainView(context: Context): ConstraintLayout(context) {
    val title = TextView(context).apply {
        id = View.generateViewId()
        textSize = 40F
        setTextColor(BLACK)
        text = "Pick The Apple"
    }
    val scoreText = TextView(context).apply {
        id = View.generateViewId()
        textSize = 40F
        setTextColor(BLACK)
        text = "0"
    }
    val gridLayout = GridLayout(context).apply {
        id = View.generateViewId()
        rowCount = 11
        columnCount = 11
    }
    val joystick = Joystick(context).apply {
        id = View.generateViewId()

    }
    val rows = 11
    val cols = 11
    val board = Array(rows) { row ->
        Array(cols) { col ->
            ImageView(context).apply {
                this.id = View.generateViewId()
            }
        }
    }

    init {
        addView(title)
        title.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = LayoutParams.PARENT_ID
            leftToLeft = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            topMargin = 32
        }
        addView(scoreText)
        scoreText.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = title.id
            leftToLeft = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            topMargin = 32
        }
        addView(gridLayout)
        gridLayout.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = scoreText.id
            leftToLeft = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            topMargin = 32
            leftMargin = 16
            rightMargin = 16
        }
        addView(joystick)
        joystick.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = gridLayout.id
            leftToLeft = LayoutParams.PARENT_ID
            rightToRight = LayoutParams.PARENT_ID
            bottomToBottom = LayoutParams.PARENT_ID
            topMargin = 16
            leftMargin = 16
            rightMargin = 16
            bottomMargin = 16
        }
    }
}