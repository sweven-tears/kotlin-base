package pers.sweven.common.utils
//
//import android.graphics.Color
//import android.view.View
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import android.widget.TextView
//import androidx.annotation.DrawableRes
//import androidx.core.view.children
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.scwang.smart.refresh.layout.SmartRefreshLayout
//import com.scwang.smart.refresh.layout.listener.OnRefreshListener
//
///**
// * Created by Sweven on 2023/10/30--14:05.
// * Email: sweventears@163.com
// */
//class SmartRefreshRecyclerHelper(val smartRefreshLayout: SmartRefreshLayout) {
//    private val context = smartRefreshLayout.context
//    private val relativeLayout: RelativeLayout = smartRefreshLayout.findViewById(R.id.smartRelativeLayout)
//    var recyclerView: RecyclerView? = null
//    var tvTips: TextView? = null
//    var includeNoData: View? = null
//    var ivNoData: ImageView? = null
//    var tvNoData: TextView? = null
//
//    var refreshEnable: Boolean = false
//        set(value) {
//            smartRefreshLayout.setEnableRefresh(value)
//            field = value
//        }
//
//    init {
//        tvTips = TextView(context).apply {
//            id = R.id.tvTips
//            textSize = 15f
//            visibility = SmartRefreshLayout.VISIBLE
//            setTextColor(Color.GRAY)
//            val params2 = RelativeLayout.LayoutParams(-2, -2)
//            params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
//            params2.topMargin = dip2px(10f)
//            relativeLayout.addView(this, params2)
//        }
//
//        for (child in relativeLayout.children) {
//            if (child is RecyclerView) {
//                recyclerView = child.apply {
//                    (layoutParams as RelativeLayout.LayoutParams)
//                        .addRule(RelativeLayout.BELOW, R.id.tvTips)
//                }
//            }
//        }
//        addNoData(relativeLayout)
//    }
//
//
//    private fun addNoData(parent: RelativeLayout) {
//        val relativeLayout = RelativeLayout(parent.context)
//        val imageView = ImageView(parent.context)
//        imageView.id = R.id.ivNoData
//        imageView.adjustViewBounds = true
//        imageView.contentDescription = "暂无更多数据"
//        RelativeLayout.LayoutParams(dip2px(100f), -2).apply {
//            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//            relativeLayout.addView(imageView, this)
//        }
//        val textView = TextView(parent.context)
//        textView.id = R.id.tvNoData
//        RelativeLayout.LayoutParams(-2, -2).apply {
//            addRule(RelativeLayout.BELOW, R.id.ivNoData)
//            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
//            topMargin = dip2px(20f)
//            relativeLayout.addView(textView, this)
//        }
//        RelativeLayout.LayoutParams(-1, -1).apply {
//            parent.addView(relativeLayout, this)
//        }
//        relativeLayout.visibility = SmartRefreshLayout.GONE
//
//        includeNoData = relativeLayout
//        ivNoData = imageView
//        tvNoData = textView
//    }
//
//    fun setHeadText(text: CharSequence) {
//        tvTips?.post {
//            tvTips?.text = text
//            if (text.trim().isNotEmpty()) {
//                tvTips?.visibility = SmartRefreshLayout.VISIBLE
//            } else {
//                tvTips?.visibility = SmartRefreshLayout.GONE
//            }
//        }
//    }
//
//    fun scrollToTop() {
//        val layoutManager = recyclerView?.layoutManager
//        if (layoutManager is LinearLayoutManager) {
//            layoutManager.scrollToPositionWithOffset(0, 0)
//        } else if (layoutManager is GridLayoutManager) {
//            layoutManager.scrollToPositionWithOffset(0, 0)
//        }
//    }
//
//    fun showNoData(show: Boolean) {
//        if (refreshEnable && tvTips?.text?.trim()?.isNotEmpty() == true) {
//            tvTips?.visibility = if (show) SmartRefreshLayout.VISIBLE else SmartRefreshLayout.GONE
//        }
//        recyclerView?.visibility = if (show) SmartRefreshLayout.GONE else SmartRefreshLayout.VISIBLE
//
//        includeNoData?.visibility =
//            if (show) SmartRefreshLayout.VISIBLE else SmartRefreshLayout.GONE
//    }
//
//    fun <T> nextPage(
//        page: GoodsPage<T>?,
//        adapter: BaseAdapter<T, *>,
//        nextPage: (page: Int) -> Unit
//    ) {
//        tvTips?.visibility = SmartRefreshLayout.GONE
//        if (page == null) {
//            if (smartRefreshLayout.isLoading == true) {
//                smartRefreshLayout.finishLoadMore(2, false, true)
//            }
//            if (smartRefreshLayout.isRefreshing == true) {
//                smartRefreshLayout.finishRefresh(false)
//            }
//            if (refreshEnable) {
//                setHeadText("下拉加载更多")
//                tvTips?.visibility = SmartRefreshLayout.VISIBLE
//            }
//            return
//        }
//        page.meta?.apply {
//            if (current_page == 1) {
//                val list = page.data.getList()
//                adapter.list = list
//                scrollToTop()
//
//                smartRefreshLayout.finishRefresh(true)
//            } else {
//                adapter.addData(page.data.getList())
//            }
//            // 判断当前recyclerview是否为空数据，并设置空提示
//            if (adapter.list.isNotEmpty()) {
//                showNoData(false)
//            } else {
//                showNoData(true)
//            }
//
//            if (current_page < last_page) {
//                smartRefreshLayout.finishLoadMore(2, true, false)
//                // 加载下一页
//                smartRefreshLayout.setEnableLoadMore(true)
//                smartRefreshLayout.setOnLoadMoreListener {
//                    nextPage.invoke(current_page + 1)
//                }
//            } else {
//                // 没有下一页的处理
//                smartRefreshLayout.setEnableLoadMore(false)
//                if (current_page > 1) {
//                    smartRefreshLayout.finishLoadMore(2, true, true)
//                }
//            }
//        }
//    }
//
//
//    fun dip2px(dipValue: Float): Int {
//        val scale = context.resources.displayMetrics.density
//        return (dipValue * scale + 0.5f).toInt()
//    }
//
//    inner class Builder {
//
//        fun setRefresh(refresh: Boolean): Builder {
//            refreshEnable = refresh
//            return this
//        }
//
//        fun setLoadMore(loadMore: Boolean): Builder {
//            smartRefreshLayout.setEnableLoadMore(loadMore)
//            return this
//        }
//
//        fun setAutoLoadMore(autoLoadMore: Boolean): Builder {
//            smartRefreshLayout.setEnableAutoLoadMore(autoLoadMore)
//            return this
//        }
//
//        fun setNoDataText(noDataText: CharSequence): Builder {
//            tvNoData?.text = noDataText
//            return this
//        }
//
//        fun setNoDataImage(@DrawableRes noDataImage: Int): Builder {
//            ivNoData?.setImageResource(noDataImage)
//            return this
//        }
//
//        /**
//         * @return 滚动反弹
//         */
//        fun setEnableOverScrollDrag(enable: Boolean): Builder {
//            smartRefreshLayout.setEnableOverScrollDrag(enable)
//            return this
//        }
//
//        /**
//         * @return 滚动拖动
//         */
//        fun setEnableOverScrollBounce(enable: Boolean): Builder {
//            smartRefreshLayout.setEnableOverScrollBounce(enable)
//            return this
//        }
//
//        fun setAdapter(adapter: BaseAdapter<*, *>): Builder {
//            recyclerView?.adapter = adapter
//            return this
//        }
//
//        fun setHeadText(text: CharSequence): Builder {
//            tvTips?.text = text
//            return this
//        }
//
//        fun setLayoutManager(layoutManager: RecyclerView.LayoutManager): Builder {
//            recyclerView?.layoutManager = layoutManager
//            return this
//        }
//
//        fun setOnRefreshListener(onRefreshListener: OnRefreshListener?): Builder {
//            smartRefreshLayout.setOnRefreshListener(onRefreshListener)
//            return this
//        }
//
//        fun setOnClickNoDataViewListener(onClickNoDataViewListener: View.OnClickListener?): Builder {
//            Utils.onClickView({
//                onClickNoDataViewListener?.onClick(it)
//            }, ivNoData, tvNoData)
//            return this
//        }
//
//    }
//
//    companion object {
//        @JvmStatic
//        fun <T> SmartRefreshLayout?.nextPage(
//            page: GoodsPage<T>?,
//            adapter: BaseAdapter<T, *>,
//            nextPage: (page: Int) -> Unit,
//        ) {
//            if (page == null) {
//                if (this?.isLoading == true) {
//                    this.finishLoadMore(2, false, true)
//                }
//                if (this?.isRefreshing == true) {
//                    this.finishRefresh(false)
//                }
//                return
//            }
//            page.meta?.apply {
//                if (current_page == 1) {
//                    val list = page.data.getList()
//                    adapter.list = list
//                    this@nextPage?.finishRefresh(true)
//                } else {
//                    adapter.addData(page.data.getList())
//                }
//
//                if (current_page < last_page) {
//                    this@nextPage?.finishLoadMore(2, true, false)
//                    // 加载下一页
//                    this@nextPage?.setEnableLoadMore(true)
//                    this@nextPage?.setOnLoadMoreListener {
//                        nextPage.invoke(current_page + 1)
//                    }
//                } else {
//                    // 没有下一页的处理
//                    this@nextPage?.setEnableLoadMore(false)
//                    if (current_page > 1) {
//                        this@nextPage?.finishLoadMore(2, true, true)
//                    }
//                }
//            }
//        }
//    }
//}