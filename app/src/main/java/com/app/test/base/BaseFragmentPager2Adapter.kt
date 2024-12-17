package com.app.test.base

import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import pers.sweven.common.utils.initTab
import java.util.*

/**
 * Created by Sweven on 2022/3/29.
 * Email:sweventears@Foxmail.com
 */
open class BaseFragmentPager2Adapter<T : Fragment> : FragmentStateAdapter {
    var list: MutableList<Pair<String, T>> = ArrayList()

    constructor(
        fragmentActivity: FragmentActivity,
        list: MutableList<Pair<String, T>>,
    ) : super(fragmentActivity) {
        this.list = list
    }

    constructor(fragmentActivity: FragmentActivity) : super(fragmentActivity)

    constructor(fragmentActivity: FragmentActivity, map: Map<String, T>) : super(fragmentActivity) {
        map.forEach { (t, u) ->
            list.add(Pair(t, u))
        }
    }

    fun setData(list: MutableList<Pair<String, T>>) {
        this.list = list
        notifyItemRangeChanged(0, this.list.size, 0)
    }

    fun setList(vararg pairs: Pair<String, T>) {
        this.list.addAll(pairs)
        notifyItemRangeChanged(0, this.list.size, 0)
    }

    fun addBean(bean: Pair<String, T>) {
        list.add(bean)
        notifyItemInserted(list.size - 1)
    }

    fun addBean(position: Int, bean: Pair<String, T>) {
        list.add(position, bean)
        notifyItemInserted(position)
    }

    fun setBean(position: Int, bean: Pair<String, T>) {
        list[position] = bean
        notifyItemInserted(position)
    }

    fun getBean(position: Int): Pair<String, T>? {
        return if (position >= list.size || position < 0) {
            null
        } else list[position]
    }

    fun getTitles(): List<String> {
        val titles = arrayListOf<String>()
        list.forEach {
            titles.add(it.first)
        }
        return titles
    }

    fun clear() {
        val count = itemCount
        list.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRangeRemoved(position, 1)
    }

    fun remove(position: Int, count: Int) {
        if (list.size < position + count) {
            return
        }
        if (position + count > position) {
            list.subList(position, position + count).clear()
        }
        notifyItemRangeRemoved(position, count)
    }

    fun build(viewPager: ViewPager2,tabLayout: TabLayout){
        val inflater = LayoutInflater.from(tabLayout.context)

        viewPager.adapter = this
        list.forEachIndexed { index, bean ->
            val layoutId = onTabLayout(bean)
            val tab = tabLayout.newTab()
            if (layoutId == 0) {
                tab.text = bean.first
                tabLayout.addTab(tab)
                return@forEachIndexed
            }
            val view = inflater.inflate(layoutId, null, false)
            if (view != null) {
                tab.customView = view
            }
            tabLayout.addTab(tab)
            if (index == 0) {
                onTabSelected(tab, index)
            } else {
                onTabUnselected(tab, index)
            }
        }
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.setCurrentItem(tab.position, true)
                this@BaseFragmentPager2Adapter.onTabSelected(tab, tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                this@BaseFragmentPager2Adapter.onTabUnselected(tab, tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                this@BaseFragmentPager2Adapter.onTabReselected(tab, tab.position)
            }
        })
        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val tab = tabLayout.getTabAt(position)
                tabLayout.selectTab(tab, true)
            }
        })
    }

    @LayoutRes
    protected open fun onTabLayout(bean: Pair<String, T>): Int {
        return 0
    }

    open fun onTabSelected(tab: TabLayout.Tab, position: Int) {}
    open fun onTabUnselected(tab: TabLayout.Tab, position: Int) {}
    fun onTabReselected(tab: TabLayout.Tab, position: Int) {}

    override fun createFragment(position: Int): Fragment {
        return list[position].second
    }

    override fun getItemCount(): Int {
        return list.size
    }
}