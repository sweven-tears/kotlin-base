package com.app.test

import androidx.core.graphics.toColorInt

/**
 * Created by Sweven on 2023/6/9.
 * Email:sweventears@Foxmail.com
 */
object Constant {
    val DEBUG: Boolean = false

    const val BASE_URL: String = "http://jrt.appapi.yxmyykj.com"

    const val SHARED_FILE_NAME: String = "shared_test"
    const val PACKAGE_NAME: String = "com.app.test"

    const val COLOR_PRIMARY: String = "#1979FE"
    val colorPrimary: Int = COLOR_PRIMARY.toColorInt()
}
