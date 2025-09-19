package com.app.test.data.network

import com.app.test.Constant
import com.app.test.data.entity.UserSession
import com.app.test.data.network.service.ApiService
import pers.sweven.common.repository.RetrofitClient

/**
 * Created by Sweven on 2025/9/12--22:09.
 * Email: sweventears@163.com
 */
object RetrofitUtils {
    private val retrofit by lazy {
        RetrofitClient.create(
            baseUrl = Constant.BASE_URL,
            interceptors = listOf(
                RetrofitClient.createDefaultHeaderInterceptor(),
                RetrofitClient.createLoggingInterceptor(),
                RetrofitClient.createAuthInterceptor {
                    UserSession.getAuthToken()
                },
                RetrofitClient.createExceptionInterceptor { chain ->
                    ApiException.printTrack(chain)
                }
            ),
        )
    }

    @JvmStatic
    val apiService by lazy { retrofit.create(ApiService::class.java) }
}