package com.mybrowser.mybrowser.ui.main

import android.arch.lifecycle.Observer
import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat.getSystemService
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.mybrowser.mybrowser.R
import com.mybrowser.mybrowser.base.BaseActivity
import com.mybrowser.mybrowser.extensions.toValidURL
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.koin.android.architecture.ext.viewModel
import org.xwalk.core.XWalkInitializer
import org.xwalk.core.XWalkView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class MainActivity : BaseActivity(), XWalkInitializer.XWalkInitListener {

    private val viewModel by viewModel<MainViewModel>()

    override fun onXWalkInitCompleted() {
        Log.d("MYCROSSWALKVIEW", "onXWalkInitCompleted")
        viewModel.goToMain.observe(this, Observer {
            replaceFragment(MainFragment.newInstance(), MainFragment.TAG)
        })
    }

    override fun onXWalkInitStarted() {
        Log.d("MYCROSSWALKVIEW", "onXWalkInitStarted")
    }

    override fun onXWalkInitCancelled() {
        Log.d("MYCROSSWALKVIEW", "onXWalkInitCancelled")
    }

    override fun onXWalkInitFailed() {
        Log.d("MYCROSSWALKVIEW", "onXWalkInitFailed()" )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MYCROSSWALKVIEW", "onCreate")


//         * Only for sdk > 19 (for test)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            val mXWalkInitializer = XWalkInitializer(this, this)
            mXWalkInitializer.initAsync()
//        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


  /*      MediaCodec.createDecoderByType("xvid")
        MediaCodec.createDecoderByType("flv1")
        MediaCodec.createDecoderByType("x-ms-mv")*/


        val codecCount = MediaCodecList.getCodecCount()
        for (i in 0 until codecCount){
            val info = MediaCodecList.getCodecInfoAt(i)
            Log.d("CODECINFO", "Name: ${info.name}")
            for (format in info.supportedTypes){
                Log.d("CODECINFO", "Format: $format")
            }
        }


        viewModel.onGoToMainFragment()

        viewModel.urlValue.observe(this, Observer {
            if (!search_bar.isFocused) {
                search_bar.setText(it)
            }
        })

        viewModel.progress.observe(this, Observer {
            it?.let {
                when (it) {
                    100 -> {
                        progress.hide()
                        stop_or_refresh.setImageResource(R.drawable.ic_refresh)
                    }
                    else -> {
                        progress.progress = it
                        stop_or_refresh.setImageResource(R.drawable.ic_cancel)
                        progress.show()
                    }
                }
            }
        })



        search_bar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("afterTextChanged", "text $s")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("onTextChanged", "text $s strt $start befor $before count $count")
                if (s?.length!! > 3 && search_bar.hasFocus()) {
                    doAsync {
                        val url = URL("http://suggestqueries.google.com/complete/search?client=firefox&q=$s")
                        val con = url.openConnection()
                        con.connectTimeout = 1500
                        con.readTimeout = 500
                        con.connect()
                        val iStream = con.getInputStream()
                        val reader = BufferedReader(InputStreamReader(iStream, "UTF-8"))
                        val sb = StringBuilder()
                        var line = reader.readLine()
                        while (line != null) {
                            sb.append(line)
                            line = reader.readLine()
                        }
                        val arrayString: String
                        val array = JSONArray(sb.toString())
                        arrayString = array[1].toString()
                        JSONArray(arrayString).apply {
                            val results = mutableListOf<String>()
                            for (i in 0 until length()) {
                                results.add(getString(i))
                            }
                            uiThread {
                                val adapter = ArrayAdapter<String>(this@MainActivity,
                                        android.R.layout.simple_dropdown_item_1line, results)
                                search_bar.setAdapter(adapter)
                                search_bar.showDropDown()
                            }
                        }
                    }
                }
            }
        })

        stop_or_refresh.setOnClickListener {
            if (viewModel.progress.value != 100){
                //stop loading
                stop_or_refresh.setImageResource(R.drawable.ic_refresh)
                viewModel.onStopLoadingClicked()
                viewModel.progress.value = 100
            }else{
                //refresh
                stop_or_refresh.setImageResource(R.drawable.ic_cancel)
                viewModel.onRefreshClicked()
            }
        }

        search_bar.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideKeyBoard()
                viewModel.urlToLoad.value = getUrlFromSearchBar()
                container.requestFocus()
                return@OnKeyListener true
            }
            false
        })

        search_bar.setOnItemClickListener { _, view, _, _ ->
            hideKeyBoard()
            viewModel.urlToLoad.value = getUrlFromSearchBar()
            container.requestFocus()
        }

        clear_button.setOnClickListener {
            search_bar.setText("")
        }


//         * Only for sdk > 19 (for test)
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
//           viewModel.goToMain.observe(this, Observer {
//               replaceFragment(MainFragment.newInstance(), MainFragment.TAG)
//            })
//        }

    }

    private fun getUrlFromSearchBar(): String{
        val searchText = search_bar.text.toString()
        val urlToLoad: String
        urlToLoad = if (Patterns.WEB_URL.matcher(searchText).matches()) {
            searchText.toValidURL()
        } else {
            "https://www.google.com/search?q=$searchText"
        }
        return urlToLoad
    }

    private fun hideKeyBoard() {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}
