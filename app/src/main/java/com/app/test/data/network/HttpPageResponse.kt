package com.app.test.data.network

/**
 * Created by Sweven on 2025/9/12--20:40.
 * Email: sweventears@163.com
 */
open class HttpPageResponse<T> @JvmOverloads constructor(
    var page: Int = 1,
    var lastPage: Int = 1,
    var pageSize: Int = 0,
    var total: Int = 0
) : HttpResponse<List<T>>()