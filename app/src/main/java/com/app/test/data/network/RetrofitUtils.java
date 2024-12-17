package com.app.test.data.network;

import com.app.test.Constant;
import com.app.test.base.BaseRetrofit;
import com.app.test.data.network.service.ApiService;

/**
 * <br/>
 * Created by Sweven on 2023/9/22--16:35.
 * Email: sweventears@163.com
 */
public class RetrofitUtils {
    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = BaseRetrofit.getRetrofit(Constant.BASE_URL).create(ApiService.class);
        }
        return apiService;
    }
}
