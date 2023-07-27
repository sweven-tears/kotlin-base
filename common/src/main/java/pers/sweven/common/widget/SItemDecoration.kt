package pers.sweven.common.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.ceil

/**
 * [spacing]=间距,
 * [color]=颜色,
 * [notLength]=分割线两头不铺满距铺满的距离,
 * [includeEdge]=是否填充边距
 * # Created by Sweven on 2023/7/27--13:11.
 * # Email: sweventears@163.com
 */
class SItemDecoration() : ItemDecoration() {
    /**
     * 间距
     */
    var spacing = 0

    /**
     * 颜色
     */
    var color = 0

    /**
     * 整个间隔不抵拢的距离
     */
    var notLength = 0

    /**
     * 是否包含边距
     */
    var includeEdge = false

    /**
     * 画笔
     */
    var mPaint: Paint? = null

    constructor(
        spacing: Int,
        color: Int = 0,
        notLength: Int = 0,
        includeEdge: Boolean = false,
    ) : this() {
        this.notLength = notLength
        this.spacing = spacing
        this.color = color
        this.includeEdge = includeEdge
        if (color != 0) {
            mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mPaint?.apply {
                this.color = color
                this.style = Paint.Style.FILL
                this.strokeWidth = (spacing * 2).toFloat()
            }
        }
    }


    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
        val itemCount = parent.adapter?.itemCount ?: 0
        if (parent.layoutManager != null) {
            if (parent.layoutManager is GridLayoutManager) {
                val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
                //九宫格ItemDecoration
                outRect.right = spacing
                outRect.bottom = spacing
                if (itemPosition % spanCount == spanCount - 1) {
                    outRect.right = 0
                }
            } else if (parent.layoutManager is LinearLayoutManager) {
                if ((parent.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.HORIZONTAL) {
                    // 水平布局
                    outRect.right = spacing
                    if (includeEdge) {
                        // 第一个item 包含边界
                        if (itemPosition == 0) {
                            outRect.left = spacing
                        }
                    } else {
                        // 最后一个item 不包含边界
                        if (itemPosition == itemCount - 1) {
                            outRect.right = 0
                        }
                    }
                } else {
                    // 纵向布局
                    outRect.bottom = spacing
                    if (includeEdge) {
                        // 第一个item 包含边界
                        if (itemPosition == 0) {
                            outRect.top = spacing
                        }
                    } else {
                        // 最后一个item 不包含边界
                        if (itemPosition == itemCount - 1) {
                            outRect.bottom = 0
                        }
                    }
                }
            } else {
                outRect.set(spacing, spacing, spacing, spacing)
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            drawGridView(c, parent)
        } else if (layoutManager is LinearLayoutManager) {
            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                drawVerticalLine(c, parent)
            } else {
                drawHorizontalLine(c, parent)
            }
        }

    }

    private fun drawGridView(canvas: Canvas, parent: RecyclerView) {
        val gridLayoutManager = parent.layoutManager as GridLayoutManager
        val childSize = parent.childCount
        val spanCount = gridLayoutManager.spanCount
        val lines = ceil(parent.childCount * 1.0 / spanCount).toInt()
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            // 只有一行
            if (lines == 1) {
                val rect = Rect()
                rect.top = child.top + layoutParams.topMargin + notLength
                rect.bottom = child.bottom + layoutParams.bottomMargin - notLength
                rect.left = child.right + layoutParams.rightMargin
                rect.right = rect.left + spacing
                if (mPaint != null) {
                    canvas.drawRect(rect, mPaint!!)
                }
            } else {
                val currentLine = ceil((i + 1) * 1.0 / spanCount).toInt()
                val rect = Rect()
                when (currentLine) {
                    1 -> {
                        // 第一行 right line
                        rect.top = child.top + layoutParams.topMargin + notLength
                        rect.bottom = child.bottom + layoutParams.bottomMargin - notLength
                        rect.left = child.right + layoutParams.rightMargin
                        rect.right = rect.left + spacing
                        if (mPaint != null) {
                            canvas.drawRect(rect, mPaint!!)
                        }
                    }
                    lines -> {
                        // 最后一行 right line
                        rect.top = child.top + layoutParams.topMargin
                        rect.bottom = child.bottom + layoutParams.bottomMargin - notLength
                        rect.left = child.right + layoutParams.rightMargin
                        rect.right = rect.left + spacing
                        if (mPaint != null) {
                            canvas.drawRect(rect, mPaint!!)
                        }

                        // bottom line
                        rect.top = child.top + layoutParams.topMargin
                        rect.bottom = rect.top + spacing
                        rect.left = child.left + layoutParams.leftMargin + notLength
                        rect.right = child.right + layoutParams.rightMargin - notLength
                        if (mPaint != null) {
                            canvas.drawRect(rect, mPaint!!)
                        }
                    }
                    else -> {
                        // right line
                        rect.top = child.top + layoutParams.topMargin
                        rect.bottom = child.bottom + layoutParams.bottomMargin
                        rect.left = child.right + layoutParams.rightMargin
                        rect.right = rect.left + spacing
                        if (mPaint != null) {
                            canvas.drawRect(rect, mPaint!!)
                        }

                        // bottom line
                        rect.top = child.bottom + layoutParams.bottomMargin
                        rect.bottom = rect.top + spacing
                        rect.left = child.left + layoutParams.leftMargin + notLength
                        rect.right = child.right + layoutParams.rightMargin - notLength
                        if (mPaint != null) {
                            canvas.drawRect(rect, mPaint!!)
                        }
                    }
                }
            }
        }
    }

    private fun drawVerticalLine(c: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            if (i + 1 < parent.childCount) {
                val rect = Rect()
                rect.top = child.bottom + layoutParams.bottomMargin
                rect.bottom = rect.top + spacing
                rect.left = child.left + layoutParams.leftMargin + notLength
                rect.right = child.right + layoutParams.rightMargin - notLength
                if (mPaint != null) {
                    c.drawRect(rect, mPaint!!)
                }
            }
        }
    }

    private fun drawHorizontalLine(c: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            if (i + 1 < parent.childCount) {
                val rect = Rect()
                rect.left = child.left + layoutParams.leftMargin
                rect.right = rect.left + spacing
                rect.top = child.top + layoutParams.topMargin + notLength
                rect.bottom = child.bottom + layoutParams.bottomMargin - notLength
                if (mPaint != null) {
                    c.drawRect(rect, mPaint!!)
                }
            }
        }
    }

}