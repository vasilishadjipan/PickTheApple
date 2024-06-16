package com.example.picktheapple

import android.content.Context
import android.graphics.Color.GRAY
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout

class Joystick(context: Context): ConstraintLayout(context) {
    val upBtn = ImageView(context).apply {
        id = View.generateViewId()
        setBackgroundColor(GRAY)
    }
    val downBtn = ImageView(context).apply {
        id = View.generateViewId()
        setBackgroundColor(GRAY)
 }
    val leftBtn = ImageView(context).apply {
        id = View.generateViewId()
        setBackgroundColor(GRAY)
    }
    val rightBtn = ImageView(context).apply {
        id = View.generateViewId()
        setBackgroundColor(GRAY)
    }
    init {
        addView(upBtn)
        upBtn.apply {
            layoutParams = LayoutParams(120, 120).apply {
                topToTop = LayoutParams.PARENT_ID
                leftToLeft = LayoutParams.PARENT_ID
                rightToRight = LayoutParams.PARENT_ID
            }
            setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.arrow_up))
        }
        addView(downBtn)
        downBtn.apply {
            layoutParams = LayoutParams(120, 120).apply {
                topToBottom = rightBtn.id
                leftToRight = rightBtn.id
                rightToLeft = leftBtn.id
                setMargins(4,4,4,0)
            }
            setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.arrow_down))

        }
        addView(leftBtn)
        leftBtn.apply {
            layoutParams = LayoutParams(120, 120).apply {
                topToBottom = upBtn.id
                rightToLeft = upBtn.id
                setMargins(0,4,4,0)
            }
            setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.arrow_left))
        }
        addView(rightBtn)
        rightBtn.apply {
            layoutParams = LayoutParams(120, 120).apply {
                topToBottom = upBtn.id
                leftToRight = upBtn.id
                setMargins(4,4,0,0)
            }
            setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.arrow_right))
        }
    }
}