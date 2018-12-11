package com.mybrowser.mybrowser.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.mybrowser.mybrowser.base.BaseFragment

class TabPagesAdapter(fm: FragmentManager, private var pages: MutableList<BaseFragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(p0: Int): Fragment {
        return pages[p0]
    }

    override fun getCount(): Int {
        return pages.size
    }

    fun addPage(page: WebViewFragment){
        pages.add(page)
        notifyDataSetChanged()
    }
    fun addPage(page: NonCrosswolkWebViewFragment){
        pages.add(page)
        notifyDataSetChanged()
    }

    fun getAt(position: Int) = pages[position]

    fun removeAt(position: Int){
        pages.sortBy { it.position }
        pages.removeAt(position)
        notifyDataSetChanged()
    }

}