package com.app.test.data.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.math.max

/**
 * 页码管理数据类型
 * 页码从 1 开始
 * Created by Sweven on 2023/9/22--10:21.
 * Email: sweventears@163.com
 */
open class Page<T> : Serializable {
    /**
     * 数据
     */
    var `data`: ArrayList<T>? = null

    var message: String? = null
    var total: Int = 0// 总数量
    var count: Int = 0// 当前页数量
    var current: Int = 0// 当前页
    var limit: Int = 0// 每页数量

    /**
     * 页码信息
     */
    var meta: Meta? = null
        get() {
            if (field != null) {
                return field
            }
            // total=32 count = 10 current = 3 limit = 10
            // 3,10*(3-1)=20,32/10+(32%10=2>0?1:0)=4,10,10*(3-1)+10-1=29,32
            limit = max(1, limit)
            val currentPage = max(current, 1)
            return Meta(
                currentPage,
                limit * (currentPage - 1),
                total / limit + (if (total % limit > 0) 1 else 0),
                "",
                limit,
                limit * (currentPage - 1) + count - 1,
                total
            ).also { field = it }
        }

    /**
     * meta
     * @param [currentPage] 当前页码
     * @param [from] 从第n条数据开始
     * @param [lastPage] 最后一页页码
     * @param [path] 路径
     * @param [perPage] 每页数据量
     * @param [to] 到第 n+perPage 条数据结束
     * @param [total] 总数据量
     * @author Create by sweven on 2023/09/01--04:08.
     * Email:sweventears@163.com
     */
    data class Meta(
        @SerializedName("current_page")
        var currentPage: Int,
        @SerializedName("from")
        var from: Int,
        @SerializedName("last_page")
        var lastPage: Int,
        @SerializedName("path")
        var path: String,
        @SerializedName("per_page")
        var perPage: Int,
        @SerializedName("to")
        var to: Int,
        @SerializedName("total")
        var total: Int
    )
}