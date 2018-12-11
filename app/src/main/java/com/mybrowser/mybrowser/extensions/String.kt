package com.mybrowser.mybrowser.extensions

fun String.toValidURL(): String{
    if (!contains("http://") && !contains("https://")){
        return "https://$this"
    }
    return this
}