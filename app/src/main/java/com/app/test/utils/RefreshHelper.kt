package com.app.test.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.app.test.R
import com.app.test.data.entity.Page
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import pers.sweven.common.base.BaseAdapter
import pers.sweven.common.utils.SmartRefreshRecyclerHelper
import pers.sweven.common.utils.Utils
import pers.sweven.common.utils.Utils.dip2px

/**
 * Created by Sweven on 2024/6/25--10:15.
 * Email: sweventears@163.com
 */
class RefreshHelper(refreshLayout: SmartRefreshLayout) :
    SmartRefreshRecyclerHelper(
        Helper(refreshLayout),
        R.id.smartRelativeLayout,
        R.id.tvTips,
        R.id.tvNoData,
        R.id.ivNoData,
        R.id.smartTop,
        TopView(refreshLayout.context).imageView(),
    ) {

    fun <T> nextPage(
        pageInfo: Page<T>?,
        adapter: BaseAdapter<T, *>,
        showHeadTips: Boolean = false,
        nextPage: (page: Int) -> Unit,
    ) {
        super.nextPage(object : PageEngine<T> {
            override fun getList(): List<T>? {
                return pageInfo?.data
            }

            override fun getMetaInfo(): PageEngine.MetaEngine? {
                return pageInfo?.meta?.let {
                    object : PageEngine.MetaEngine {
                        override fun getCurrent(): Int {
                            return it.currentPage
                        }

                        override fun getLast(): Int {
                            return it.lastPage
                        }
                    }
                }
            }
        }, adapter, showHeadTips, nextPage)
    }

    override fun setNoData(parent: RelativeLayout): Pair<ViewGroup, Pair<ImageView, TextView>> {
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
            addRule(RelativeLayout.BELOW, ivNoDataId)
            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            relativeLayout.addView(textView, this)
        }
        RelativeLayout.LayoutParams(-1, -1).apply {
            parent.addView(relativeLayout, this)
        }
        relativeLayout.visibility = View.GONE
        return Pair(relativeLayout, (imageView to textView))
    }

    private class TopView(private val context: Context) {
        fun imageView(): (RelativeLayout) -> ImageView {
            return {
                ImageView(context).apply {
                    setBackgroundResource(R.drawable.corner200_primary)
                    Utils.tintColor(this.background, Color.parseColor("#26000000"))
                    setImageResource(R.drawable.ic_top_up)
                    Utils.tintColor(this.drawable, Color.WHITE)
                    setPadding(30)
                    val params = RelativeLayout.LayoutParams(-2, -2)
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                    params.bottomMargin = dip2px(context, 10f)
                    params.marginEnd = dip2px(context, 10f)
                    it.addView(this, params)
                }
            }
        }
    }

    private class Helper(val smartRefreshLayout: SmartRefreshLayout) : RefreshLayout {
        override fun isRefreshing(): Boolean {
            return smartRefreshLayout.isRefreshing
        }

        override fun isLoading(): Boolean {
            return smartRefreshLayout.isLoading
        }

        override fun getContext(): Context {
            return smartRefreshLayout.context
        }

        override fun setOnRefreshListener(listener: () -> Unit) {
            smartRefreshLayout.setOnRefreshListener {
                listener.invoke()
            }
        }

        override fun <T> findViewById(id: Int): T {
            return smartRefreshLayout.findViewById(id) as T
        }

        override fun setEnableRefresh(enable: Boolean) {
            smartRefreshLayout.setEnableRefresh(enable)
        }

        override fun finishRefresh(success: Boolean) {
            smartRefreshLayout.finishRefresh(success)
        }

        override fun setEnableLoadMore(enable: Boolean) {
            smartRefreshLayout.setEnableLoadMore(enable)
        }

        override fun finishLoadMore(delayed: Int, success: Boolean, noMoreData: Boolean) {
            smartRefreshLayout.finishLoadMore(delayed, success, noMoreData)
        }

        override fun setOnLoadMoreListener(listener: () -> Unit) {
            smartRefreshLayout.setOnLoadMoreListener {
                listener.invoke()
            }
        }

        override fun setEnableAutoLoadMore(autoLoadMore: Boolean) {
            smartRefreshLayout.setEnableAutoLoadMore(autoLoadMore)
        }

        override fun setEnableOverScrollDrag(enable: Boolean) {
            smartRefreshLayout.setEnableOverScrollDrag(enable)
        }

        override fun setEnableOverScrollBounce(enable: Boolean) {
            smartRefreshLayout.setEnableOverScrollBounce(enable)
        }

        override fun setNoMoreData(show: Boolean) {
            smartRefreshLayout.setNoMoreData(show)
        }
    }
}