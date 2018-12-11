package com.mybrowser.mybrowser.base

import android.support.v7.app.AppCompatActivity
import com.mybrowser.mybrowser.R
import com.mybrowser.mybrowser.utils.showToast

abstract class BaseActivity : AppCompatActivity() {

    protected fun replaceFragment(fragment: BaseFragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit()
    }

    protected fun addFragment(fragment: BaseFragment, tag: String) {
        supportFragmentManager.beginTransaction().add(R.id.container, fragment, tag)
                .addToBackStack("fragment").commit()
    }

    fun showMessage(message: String, duration: Int){
        showToast(this, message, duration)
    }
}