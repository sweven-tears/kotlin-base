package com.app.test.data.network

/**
 * Created by Sweven on 2025/9/12--20:39.
 * Email: sweventears@163.com
 */
open class HttpResponse<T> @JvmOverloads constructor(
    var code: Int = 0,
    var message: String? = null,
    var data: T? = null,
)