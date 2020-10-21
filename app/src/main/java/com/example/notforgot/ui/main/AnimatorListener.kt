package com.example.notforgot.ui.main

import android.animation.Animator

interface AnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {
        return
    }

    override fun onAnimationCancel(animation: Animator?) {
        return
    }

    override fun onAnimationRepeat(animation: Animator?) {
        return
    }
}