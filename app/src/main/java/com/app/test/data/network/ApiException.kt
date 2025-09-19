package com.app.test.data.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


// 自定义API异常
class ApiException(
    val path: String,
    val statusCode: Int,
    val entity: ExceptionEntity,
) : IOException("API Error: $path [$statusCode] - ${entity.message ?: "Unknown error"}") {
    companion object {
        @Throws(IOException::class)
        fun printTrack(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            return try {
                val code = response.code
                if (code != 200) {
                    val content = String(response.body?.bytes() ?: ByteArray(0))
                    val entity =
                        Gson().fromJson(content, ExceptionEntity::class.java) ?: ExceptionEntity()
                    throw ApiException(request.url.encodedPath, code, entity)
                } else {
                    response
                }
            } catch (e: Exception) {
                throw e
            }
        }

        private fun logError(request: Request, response: Response, content: String) {
            // 实际项目中替换为您的日志系统
            println("""
        |=== HTTP ERROR ===
        |URL: ${request.url}
        |Method: ${request.method}
        |Code: ${response.code}
        |Message: ${response.message}
        |Body: $content
        |===================
        """.trimMargin())
        }
    }

    // 异常实体类
    data class ExceptionEntity(
        var code: Int = 0,
        var message: String? = null,
        var timestamp: Long = System.currentTimeMillis(),
    )
}
