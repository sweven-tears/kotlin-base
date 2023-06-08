package pers.sweven.common.repository.interceptor;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pers.sweven.common.utils.NetworkUtils;

public class CacheInterceptor implements Interceptor {

    private final Context context;

    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetworkUtils.isNetworkAvailable(context)) {
            Response response = chain.proceed(request);
            int maxAge = 60;
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            return chain.proceed(request);
        }
    }
}