package pers.sweven.common.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pers.sweven.common.base.BaseAdapter

/**
 * Created by Sweven on 2023/10/30--14:05.
 * Email: sweventears@163.com
 */
abstract class SmartRefreshRecyclerHelper(
    val refreshLayout: RefreshLayout,
    relativeLayoutId: Int,
    val tvTipsId: Int,
    val tvNoDataId: Int,
    val ivNoDataId: Int,
    val smartTopId: Int,
    val smartTopView: ((RelativeLayout) -> ImageView?)?,
) {
    private val GONE = View.GONE
    private val VISIBLE = View.VISIBLE

    private val context = refreshLayout.getContext()
    private val relativeLayout: RelativeLayout =
        refreshLayout.findViewById(relativeLayoutId) as RelativeLayout
    lateinit var recyclerView: RecyclerView
    var tvTips: TextView
    var includeNoData: View? = null
    var ivNoData: ImageView? = null
    var tvNoData: TextView? = null
    private var noDataImage: Int = 0
    private var noDataText: CharSequence = ""

    var refreshEnable: Boolean = false
        set(value) {
            refreshLayout.setEnableRefresh(value)
            field = value
        }

    init {
        tvTips = TextView(context).apply {
            id = tvTipsId
            textSize = 15f
            visibility = View.VISIBLE
            setTextColor(Color.GRAY)
            val params2 = RelativeLayout.LayoutParams(-2, -2)
            params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            params2.topMargin = dip2px(10f)
            relativeLayout.addView(this, params2)
        }

        for (child in relativeLayout.children) {
            if (child is RecyclerView) {
                recyclerView = child.apply {
                    (layoutParams as RelativeLayout.LayoutParams)
                        .addRule(RelativeLayout.BELOW, tvTipsId)
                }
            }
        }
        addNoData(relativeLayout)

        recyclerView
    }


    private fun addNoData(parent: RelativeLayout) {
        val relativeLayout = RelativeLayout(parent.context)
        val imageView = ImageView(parent.context)
        imageView.id = ivNoDataId
        imageView.adjustViewBounds = true
        imageView.contentDescription = "暂无更多数据"
        RelativeLayout.LayoutParams(dip2px(200f), -2).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            relativeLayout.addView(imageView, this)
        }
        val textView = TextView(parent.context)
        textView.id = tvNoDataId
        RelativeLayout.LayoutParams(-2, -2).apply {
            addRule(RelativeLayout.ALIGN_BOTTOM, ivNoDataId)
            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            relativeLayout.addView(textView, this)
        }
        RelativeLayout.LayoutParams(-1, -1).apply {
            parent.addView(relativeLayout, this)
        }
        relativeLayout.visibility = GONE

        includeNoData = relativeLayout
        ivNoData = imageView
        tvNoData = textView
    }

    fun setHeadText(text: CharSequence) {
        tvTips.post {
            tvTips.text = text
            if (text.trim().isNotEmpty()) {
                tvTips.visibility = VISIBLE
            } else {
                tvTips.visibility = GONE
            }
        }
    }

    fun scrollToTop() {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            layoutManager.scrollToPositionWithOffset(0, 0)
        } else if (layoutManager is GridLayoutManager) {
            layoutManager.scrollToPositionWithOffset(0, 0)
        }
    }

    fun showNoData(show: Boolean) {
        if (refreshEnable && tvTips?.text?.trim()?.isNotEmpty() == true) {
            tvTips?.visibility = if (show) VISIBLE else GONE
        }
        recyclerView.visibility = if (show) GONE else VISIBLE

        includeNoData?.visibility = if (show) VISIBLE else GONE

        if (show) {
            ivNoData?.setImageResource(noDataImage)
            tvNoData?.text = noDataText
        }
    }

    fun showNoData(noData: Pair<Int, CharSequence>) {
        if (refreshEnable && tvTips?.text?.trim()?.isNotEmpty() == true) {
            tvTips?.visibility = GONE
        }

        recyclerView.visibility = GONE

        includeNoData?.visibility = VISIBLE

        ivNoData?.setImageResource(noData.first)
        tvNoData?.text = noData.second
    }

    fun addScrollTopButton(): View? {
        var view0 = relativeLayout.findViewById<ImageView>(smartTopId)
        if (view0 == null) {
            view0 = smartTopView?.invoke(relativeLayout)?.apply {
                this.id = smartTopId
            }
        }

        view0.onClickView {
            scrollToTop()
            it.isVisible = false
        }
        return view0
    }

    /**
     * 下一页||  please rewrite this method
     * @param [page] 页
     * @param [adapter] 适配器
     * @param [showHeadTips] 显示头提示
     * @param [nextPage] 下一页
     */
    protected fun <T> nextPage(
        page: PageEngine<T>?,
        adapter: BaseAdapter<T, *>,
        showHeadTips: Boolean,
        nextPage: (page: Int) -> Unit,
    ) {
        tvTips.visibility = GONE
        if (page == null) {
            if (refreshLayout.isLoading()) {
                refreshLayout.finishLoadMore(2, false, true)
            }
            if (refreshLayout.isRefreshing()) {
                refreshLayout.finishRefresh(false)
            }
            if (refreshEnable && adapter.itemCount == 0) {
                refreshLayout.setEnableLoadMore(false)
                if (showHeadTips) {
                    setHeadText("下拉刷新")
                    tvTips.visibility = VISIBLE
                }
            }
            return
        }
        page.getMetaInfo()?.apply {
            if (getCurrent() == 1) {
                val list = page.getList().getList()
                adapter.list = list
                scrollToTop()

                refreshLayout.finishRefresh(true)
            } else {
                adapter.addData(page.getList().getList())
            }
            // 判断当前recyclerview是否为空数据，并设置空提示
            if (adapter.list.isNotEmpty()) {
                showNoData(false)
            } else {
                showNoData(true)
            }

            if (getCurrent() < getLast()) {
                // 加载下一页
                refreshLayout.setEnableLoadMore(true)
                refreshLayout.setNoMoreData(false)
                if (refreshLayout.isLoading()) {
                    refreshLayout.finishLoadMore(2, true, false)
                }
                refreshLayout.setOnLoadMoreListener {
                    nextPage.invoke(getCurrent() + 1)
                }
            } else {
                // 没有下一页的处理
                refreshLayout.setEnableLoadMore(false)
                refreshLayout.setNoMoreData(true)
                if (getCurrent() > 1 && refreshLayout.isLoading()) {
                    refreshLayout.finishLoadMore(2, true, true)
                }
            }
        }
    }

    fun builder(): Builder {
        return Builder()
    }

    fun dip2px(dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    inner class Builder {
        fun setRefresh(refresh: Boolean): Builder {
            refreshEnable = refresh
            return this
        }

        fun setLoadMore(loadMore: Boolean): Builder {
            refreshLayout.setEnableLoadMore(loadMore)
            return this
        }

        fun setAutoLoadMore(autoLoadMore: Boolean): Builder {
            refreshLayout.setEnableAutoLoadMore(autoLoadMore)
            return this
        }

        fun setNoData(noData: Pair<Int, CharSequence>): Builder {
            noDataImage = noData.first
            noDataText = noData.second
            tvNoData?.text = noData.second
            ivNoData?.setImageResource(noData.first)
            return this
        }

        fun setNoDataText(text: CharSequence): Builder {
            noDataText = text
            tvNoData?.text = text
            return this
        }

        fun setNoDataImage(@DrawableRes image: Int): Builder {
            noDataImage = image
            ivNoData?.setImageResource(image)
            return this
        }

        /**
         * @return 滚动反弹
         */
        fun setEnableOverScrollDrag(enable: Boolean): Builder {
            refreshLayout.setEnableOverScrollDrag(enable)
            return this
        }

        /**
         * @return 滚动拖动
         */
        fun setEnableOverScrollBounce(enable: Boolean): Builder {
            refreshLayout.setEnableOverScrollBounce(enable)
            return this
        }

        fun setAdapter(adapter: BaseAdapter<*, *>): Builder {
            recyclerView.adapter = adapter
            return this
        }

        fun setHeadText(text: CharSequence): Builder {
            tvTips.text = text
            return this
        }

        fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): Builder {
            recyclerView.layoutManager = layoutManager
            return this
        }

        fun setOnRefreshListener(onRefreshListener: () -> Unit): Builder {
            refreshLayout.setOnRefreshListener(onRefreshListener)
            return this
        }

        fun setOnClickNoDataViewListener(onClickNoDataViewListener: View.OnClickListener?): Builder {
            Utils.onClickView({
                onClickNoDataViewListener?.onClick(it)
            }, ivNoData, tvNoData)
            return this
        }

        fun setFast2Top(fast2Top: Boolean, topCount: Int = 10): Builder {
            if (!fast2Top) {
                val view = relativeLayout.findViewById<View>(smartTopId)
                if (view != null) {
                    relativeLayout.removeView(view)
                }
                return this
            }
            val button = addScrollTopButton().also { it?.isVisible = false }
            if (button != null) {
                recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager
                        var position = 0
                        if (layoutManager is LinearLayoutManager) {
                            position = layoutManager.findFirstVisibleItemPosition()
                        }
                        button.isVisible = position > topCount
                    }
                })
            }
            return this
        }
    }

    interface RefreshLayout {
        fun isRefreshing(): Boolean
        fun isLoading(): Boolean
        fun getContext(): Context
        fun setOnRefreshListener(listener: () -> Unit)
        fun <T> findViewById(id: Int): T
        fun setEnableRefresh(enable: Boolean)
        fun finishRefresh(success: Boolean)
        fun setEnableLoadMore(enable: Boolean)
        fun finishLoadMore(delayed: Int, success: Boolean, noMoreData: Boolean)
        fun setOnLoadMoreListener(listener: () -> Unit)
        fun setEnableAutoLoadMore(autoLoadMore: Boolean)
        fun setEnableOverScrollDrag(enable: Boolean)
        fun setEnableOverScrollBounce(enable: Boolean)
        fun setNoMoreData(show: Boolean)
    }

    interface PageEngine<T> {

        fun getList(): List<T>?

        fun getMetaInfo(): MetaEngine?

        interface MetaEngine {
            fun getCurrent(): Int

            fun getLast(): Int
        }
    }

}