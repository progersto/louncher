package com.mybrowser.mybrowser.custom

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Scroller

class NonSwipableViewPager: ViewPager{

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setMyScroller()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //Remove touch processing to dismiss scrolling by swipe
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        //Remove touch processing to dismiss scrolling by swipe
        return false
    }

    //Set scrolling params
    private fun setMyScroller(){
        try {
            val viewPager = ViewPager::class.java
            val scroller = viewPager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller.set(this, MyScroller(context))
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    inner class MyScroller(context: Context) : Scroller(context){
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, 350)
        }
    }
}