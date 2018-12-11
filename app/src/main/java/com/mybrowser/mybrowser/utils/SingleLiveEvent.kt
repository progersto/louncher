package com.mybrowser.mybrowser.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T>: MutableLiveData<T>(){

    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (hasActiveObservers()){

        }
        super.observe(owner, Observer {
            if (pending.compareAndSet(true, false)){
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    @MainThread
    fun call(){
        value = null
    }

    @MainThread
    fun callWithValue(t: T?){
        value = t
    }
}