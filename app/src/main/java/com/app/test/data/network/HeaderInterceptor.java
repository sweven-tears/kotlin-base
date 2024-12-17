package com.app.test.data.network;

import android.text.TextUtils;

import com.app.test.data.local.LocalManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头拦截器
 */
public class HeaderInterceptor implements Interceptor {
    private final HashMap<String, Object> headers;

    public HeaderInterceptor(HashMap<String, Object> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // header path
        List<String> pathSegments = chain.request().url().pathSegments();
        StringBuilder builder = new StringBuilder();
        for (String segment : pathSegments) {
            builder.append("/").append(segment);
        }
        String path = builder.toString();

        Request.Builder requestBuilder = chain.request().newBuilder();
        //如果公共请求头不为空,则构建新的请求
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        String token = LocalManager.getInstance().getToken();
        if (token != null) {
            requestBuilder.addHeader("Authorization", token);
        }
        requestBuilder.addHeader("path", path);
        Request request = requestBuilder.build();
        Response.Builder responseBuilder = chain.proceed(request).newBuilder();

        if (!TextUtils.isEmpty(request.cacheControl().toString())) {
            responseBuilder
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + request.cacheControl().maxAgeSeconds());
        }
        return responseBuilder.build();
    }
}