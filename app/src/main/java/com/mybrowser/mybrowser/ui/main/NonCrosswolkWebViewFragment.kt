package com.mybrowser.mybrowser.ui.main

import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.mybrowser.mybrowser.R
import com.mybrowser.mybrowser.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_noncrosswalk.*
import kotlinx.android.synthetic.main.fragment_webview.*
import org.koin.android.architecture.ext.sharedViewModel
import org.xwalk.core.XWalkResourceClient
import org.xwalk.core.XWalkUIClient
import org.xwalk.core.XWalkView

class NonCrosswolkWebViewFragment: BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private var url: String? = null

    var mime = "text/html"
    var encoding = "utf-8"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val mXWalkInitializer = XWalkInitializer(this, this.context)
        // mXWalkInitializer.initAsync()

        return inflater.inflate(R.layout.fragment_noncrosswalk, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        noncrosswalk_webview.visibility = View.VISIBLE
        position = arguments?.getInt(KEY_POSITION)
        Log.d("MYFRAGMENT", "Position: $position")

        viewModel.stopLoading.observe(viewLifecycleOwner, Observer {
            noncrosswalk_webview.stopLoading()
        })

/*        viewModel.refresh.observe(viewLifecycleOwner, Observer {
            noncrosswalk_webview.reload(Web)
        })*/
        noncrosswalk_webview.settings.javaScriptEnabled = true
        noncrosswalk_webview.settings.pluginState = WebSettings.PluginState.ON
        noncrosswalk_webview.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                viewModel.progress.value = newProgress
            }

        }
        noncrosswalk_webview.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
        noncrosswalk_webview.webViewClient = object : WebViewClient(){
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                noncrosswalk_webview.loadDataWithBaseURL("about:blank","<html>" +
                        "<head>" +
                        "<meta charset=\"utf8\"/>" +
                        "<style type=\"text/css\">" +
                        "div{" +
                        "padding: 5%;" +
                        "}" +
                        "h1{" +
                        "font-size: 400%;" +
                        "margin-bottom: 0px;" +
                        "margin-top: 0px;" +
                        "}" +
                        "h2{" +
                        "margin-top: 0px;" +
                        "font-size: 200%;" +
                        "}" +
                        "</style/>" +
                        "</head>" +
                        "<body>" +
                        "<div>" +
                        "<h1>Something went wrong</h1>" +
                        "</br>" +
                        "<h2>Error: $error</h2>" +
                        "</div>" +
                        "</body>" +
                        "</html>", mime,encoding,null)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed();
                super.onReceivedSslError(view, handler, error)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                viewModel.urlValue.value = url
                this@NonCrosswolkWebViewFragment.url = url
                receiveTitle()
                super.onLoadResource(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                Log.d("MLOADING", "Fragment: ${this@NonCrosswolkWebViewFragment.url}: $url")
                viewModel.urlValue.value = url
                this@NonCrosswolkWebViewFragment.url = url
                position?.let {
                    viewModel.loadingCallback.value = Pair(it, url ?: "")
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

        }



        if (url != null) {
            noncrosswalk_webview.loadUrl(url)
        } else {
            noncrosswalk_webview.loadUrl("https://google.com", null)
        }
    }
    override fun getWebView(): View{
        return noncrosswalk_webview
    }
    override fun load(url: String) {
        noncrosswalk_webview?.loadUrl(url)
        this.url = url
    }

    override fun getUrl() = noncrosswalk_webview?.url

    fun receiveTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            noncrosswalk_webview?.evaluateJavascript("document.title;") {
                Log.d("MYTITLE", "Title is: $it")
                val title = it
                position?.let {
                    viewModel.loadingCallback.value = Pair(it, title)
                }

            }
        }
    }

    companion object {
        private const val KEY_POSITION = "key_position"
        const val TAG = "NonCrosswolkWebViewFragmen"
        fun newInstance(position: Int) = NonCrosswolkWebViewFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_POSITION, position)
            }
        }
    }
}