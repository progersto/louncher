package com.mybrowser.mybrowser.ui.main

import android.arch.lifecycle.MutableLiveData
import com.mybrowser.mybrowser.base.BaseViewModel
import com.mybrowser.mybrowser.utils.SingleLiveEvent

class MainViewModel: BaseViewModel(){
    val goToMain = SingleLiveEvent<Unit>()

    val stopLoading = SingleLiveEvent<Unit>()
    val refresh = SingleLiveEvent<Unit>()

    val urlValue = MutableLiveData<String>()
    val progress = MutableLiveData<Int>()
    val urlToLoad = MutableLiveData<String>()
    val loadingCallback = MutableLiveData<Pair<Int, String>>()

    fun onGoToMainFragment(){
        goToMain.call()
    }

    fun onStopLoadingClicked(){
        stopLoading.call()
    }

    fun onRefreshClicked(){
        refresh.call()
    }
}