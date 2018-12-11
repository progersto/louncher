package com.mybrowser.mybrowser.ui.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mybrowser.mybrowser.R
import com.mybrowser.mybrowser.R.id.pages
import com.mybrowser.mybrowser.R.id.tabs
import com.mybrowser.mybrowser.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.tab.view.*
import org.koin.android.architecture.ext.sharedViewModel

class MainFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = TabPagesAdapter(childFragmentManager, mutableListOf())
        pages.adapter = adapter

        viewModel.loadingCallback.observe(viewLifecycleOwner, Observer {
            it?.let {
                tabs.getTabAt(it.first)?.customView?.title?.text = it.second
            }
        })

//         Only for sdk > 19 (for test)
//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
//                adapter.addPage(NonCrosswolkWebViewFragment.newInstance(adapter.count))
//                addTab(adapter)
//        }else{
            adapter.addPage(WebViewFragment.newInstance(adapter.count))
            addTab(adapter)
//        }


        viewModel.urlToLoad.observe(viewLifecycleOwner, Observer {
            Log.d("MYURL", "Url to load: $it")
            it?.let {
                adapter.getAt(tabs.selectedTabPosition).load(it)
            }
        })

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.let {
                    Log.d("MYTABS", "Setting position: ${it.position}")
                    pages.setCurrentItem(it.position, false)
                    viewModel.urlValue.value = adapter.getAt(it.position).getUrl()
                }
            }
        })
        add_button.setOnClickListener {
            if (adapter.count <= 5) {
                adapter.addPage(WebViewFragment.newInstance(adapter.count))
                addTab(adapter)
            }
        }
    }

    private fun addTab(adapter: TabPagesAdapter) {
        val tab = tabs.newTab().setText("New tab")
        val tabCustomView = View.inflate(context, R.layout.tab, null)
        tabCustomView.button_close.setOnClickListener {
            showMessage("Removing ${tab.position} tab. Url: ${adapter.getAt(tab.position).getUrl()}")
            if (tabs.tabCount > 1) {
                Log.d("MYTABS", "Removing: ${tab.position}")
                adapter.destroyItem(pages, tab.position, adapter.getAt(tab.position))
                pages.removeViewAt(tab.position)
                childFragmentManager.beginTransaction().remove(adapter.getAt(tab.position)).commit()
                adapter.removeAt(tab.position)
                for (i in 0 until adapter.count) {
                    adapter.getAt(i).position = i
                }
                tabs.removeTabAt(tab.position)
            }
        }

        tab.customView = tabCustomView
        tabs.addTab(tab, true)
    }

    companion object {
        const val TAG = "MainFragment"
        fun newInstance() = MainFragment()
    }
}