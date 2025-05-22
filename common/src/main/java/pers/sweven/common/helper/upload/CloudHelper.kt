package com.shop.core.sdk.helper.upload

import java.io.File

/**
 * Created by Sweven on 2025/4/2--11:15.
 * Email: sweventears@163.com
 */
class CloudHelper(private val factory: Factory) {
    private var path = ""
    private var toUrl = ""

    fun setPath(path: String): CloudHelper {
        this.path = path
        return this
    }

    fun setToUrl(toUrl: String): CloudHelper {
        this.toUrl = toUrl
        return this
    }


    fun upload(successCall: (url: String) -> Unit) {
        factory.upload(File(path), toUrl)
        factory.success = successCall
    }

    fun process(process: (progress: Long, total: Long) -> Unit): CloudHelper {
        factory.process = process
        return this
    }

    fun upload(call: (url: String) -> Unit, throws: (e: Exception) -> Unit) {
        factory.upload(File(path), toUrl)
        factory.error = throws
        factory.success = call
    }


    interface Factory {
        var process: (progress: Long, total: Long) -> Unit
        var success: (url: String) -> Unit
        var error: (e: Exception) -> Unit
        var cancel: () -> Unit

        fun init()

        fun upload(file: File, toUrl: String)
    }
}