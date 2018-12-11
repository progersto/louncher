package com.mybrowser.mybrowser.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import org.xwalk.core.XWalkView

abstract class BaseFragment: Fragment(){

    protected val viewLifecycleOwner = ViewLifecycleOwner()
    protected var baseActivity: BaseActivity? = null
    var position: Int? = 0

    open fun getWebView(): View?{
        return null
    }
    open fun getUrl() :String? {
        return null
    }
    open fun load(url: String){
        if (getWebView() is WebView){
            (getWebView() as WebView).loadUrl(url)
        }else{
            (getWebView() as XWalkView).loadUrl(url)
        }


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        super.onStart()
    }

    override fun onResume() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        super.onResume()
    }

    override fun onPause() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        try {
            (getWebView() as XWalkView).navigationHistory.clear()
            Log.d("MYCROSSWALK","History cleared")
        }catch (e: Exception){
            Log.d("MYCROSSWALK","error while clear history")
        }

        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroyView()
    }

    protected fun showMessage(message: String, duration: Int = Toast.LENGTH_SHORT){
        baseActivity?.showMessage(message, duration)
    }

    inner class ViewLifecycleOwner: LifecycleOwner{
        private val lifecycleRegistry = LifecycleRegistry(this)

        override fun getLifecycle() = lifecycleRegistry
    }
}