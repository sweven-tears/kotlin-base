package com.app.test.data.network.service

import com.app.test.data.local.User
import com.app.test.data.network.HttpPageResponse
import retrofit2.http.GET

/**
 * Created by Sweven on 2024/11/7--11:26.
 * Email: sweventears@163.com
 */
interface ApiService {
    @GET("api/users")
    suspend fun getUsers(): HttpPageResponse<User>
}