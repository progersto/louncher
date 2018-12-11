package com.mybrowser.mybrowser.ui.main

import android.arch.lifecycle.Observer
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.mybrowser.mybrowser.R
import com.mybrowser.mybrowser.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_webview.*
import org.chromium.base.Log
import org.chromium.content.browser.ContentViewRenderView
import org.jetbrains.anko.Android
import org.jetbrains.anko.videoView
import org.koin.android.architecture.ext.sharedViewModel
import org.xwalk.core.*
import org.xwalk.core.XWalkInitializer.XWalkInitListener

class WebViewFragment : BaseFragment()/*, XWalkInitListener*/ {
  /*  override fun onXWalkInitCompleted() {
        Log.d("dddd", "onXWalkInitCompleted")

    }

    override fun onXWalkInitStarted() {
        Log.d("dddd", "onXWalkInitStarted")
    }

    override fun onXWalkInitCancelled() {
        Log.d("dddd", "onXWalkInitStarted")
    }

    override fun onXWalkInitFailed() {
        Log.d("dddd", "onXWalkInitFailed")
    }*/

    private val viewModel by sharedViewModel<MainViewModel>()
    private var url: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //val mXWalkInitializer = XWalkInitializer(this, this.context)
       // mXWalkInitializer.initAsync()
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        position = arguments?.getInt(KEY_POSITION)
        Log.d("MYFRAGMENT", "Position: $position")

        viewModel.stopLoading.observe(viewLifecycleOwner, Observer {
            webview.stopLoading()
        })


        viewModel.refresh.observe(viewLifecycleOwner, Observer {
            webview.reload(XWalkView.RELOAD_NORMAL)
        })
        webview.settings.javaScriptEnabled = true
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true)


        Log.d("MYCROSSWALK","webview.compositingSurfaceType ${webview.compositingSurfaceType}")

        webview.setUIClient(XWalkUIClient(webview))
        webview.setResourceClient(object : XWalkResourceClient(webview) {
            override fun onProgressChanged(view: XWalkView?, progressInPercent: Int) {
                super.onProgressChanged(view, progressInPercent)
                viewModel.progress.value = progressInPercent
            }
            override fun onReceivedLoadError(view: XWalkView?, errorCode: Int, description: String?, failingUrl: String?) {
                webview.load("about:blank", "<html>" +
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
                        "<h2>Error: $description</h2>" +
                        "</div>" +
                        "</body>" +
                        "</html>")
            }

            override fun onLoadStarted(view: XWalkView?, url: String?) {
                super.onLoadStarted(view, url)
               var agent =  webview.userAgentString
                Log.d("dddddd",  agent)
                webview.userAgentString = "\"Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.63 Safari/537.31"
            }

            override fun onReceivedSslError(view: XWalkView?, callback: ValueCallback<Boolean>?, error: SslError?) {
                callback?.onReceiveValue(true)
            }

            override fun onLoadFinished(view: XWalkView?, url: String?) {
                viewModel.urlValue.value = url
                this@WebViewFragment.url = url
                receiveTitle()
                super.onLoadFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: XWalkView?, url: String?): Boolean {
                Log.d("MLOADING", "Fragment: ${this@WebViewFragment.url}: $url")
                viewModel.urlValue.value = url
                this@WebViewFragment.url = url
                position?.let {
                    viewModel.loadingCallback.value = Pair(it, url ?: "")
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        })



        if (url != null) {
            webview.load(url, null)
        } else {
            webview.load("https://google.com", null)
        }
    }

    override fun load(url: String) {
        webview?.loadUrl(url)
        this.url = url
    }

    override fun getUrl() = webview?.url

    override fun getWebView(): View{
        return webview
    }

    fun receiveTitle() {
        webview?.evaluateJavascript("document.title;") {
            Log.d("MYTITLE", "Title is: $it")
            val title = it
            position?.let {
                viewModel.loadingCallback.value = Pair(it, title)
            }

        }
    }

    companion object {
        private const val KEY_POSITION = "key_position"
        const val TAG = "WebViewFragment"
        fun newInstance(position: Int) = WebViewFragment().apply {
            arguments = Bundle().apply {
                putInt(KEY_POSITION, position)
            }
        }
    }
}