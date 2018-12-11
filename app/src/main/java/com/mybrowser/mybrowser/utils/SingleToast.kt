package com.mybrowser.mybrowser.utils

import android.content.Context
import android.widget.Toast

var toast: Toast? = null

fun showToast(context: Context, message: String, duration: Int){
    toast?.cancel()
    toast = Toast.makeText(context, message, duration)
    toast?.show()
}