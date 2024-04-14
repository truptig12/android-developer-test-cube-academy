package com.cube.cubeacademy.utils

import android.util.Log

class AndroidLogger:Logger {
    override fun error(tag: String, message: String) {
        Log.e(tag, message)
    }
}